package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_culture_tool_a.Models.DomandeMultiple;
import com.example.e_culture_tool_a.Models.DomandeTempo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.ArrayList;



public class UpdateTempoDomandeActivity extends AppCompatActivity {

    private final String TAG = "UPDATE_TEMPO";

    String tempoDomandeID;
    String author;
    String luogoID;
    String oggettoID;
    String zonaID;
    Integer tempo;
    ArrayList<DomandeMultiple> dm = new ArrayList<>();
    EditText mnome, msecondi;
    Button mSubmitButton, mremoveDomande;
    DomandeTempo dt;

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String user_id;
    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tempo_domande);

        Gson gson = new Gson();
        dt = gson.fromJson(getIntent().getStringExtra("myjson"), DomandeTempo.class);

        mnome = findViewById(R.id.NomeDomandaTempo);
        msecondi = findViewById(R.id.UpdateSecondiDomandaTempo);
        mSubmitButton = findViewById(R.id.UpdateSubmitDomandaTempo);
        mremoveDomande = findViewById(R.id.removeDomande);


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();

        mFirestoreList=findViewById(R.id.UpdaterecyclerDomandeMUltiple);

        String a = String.valueOf(dt.getTempo());
        dm = dt.getDm();

        Log.d(TAG," sm "+dm);

        mnome.setText(dt.getNome());
        msecondi.setText(a);

        Query query=fStore.collectionGroup("DomandeMultiple").whereEqualTo("oggettoID", dt.getOggettoID());
        FirestoreRecyclerOptions<DomandeMultiple> options=new FirestoreRecyclerOptions.Builder<DomandeMultiple>().setQuery(query, DomandeMultiple.class).build();

        adapter= new FirestoreRecyclerAdapter<DomandeMultiple, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.domandamultipla_single_item,parent,false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull DomandeMultiple model) {

                holder.list_nome.setText(model.getNome());
                for(int i = 0 ; i<dm.size(); i++)
                {

                    if( dm.get(i).getId().equals(model.getId())){
                        Log.d(TAG," dm dentro onbiend "+dm.get(i).getId());
                        Log.d(TAG," model dentro onbiend "+model.getId());
                        holder.list_check.setVisibility(View.VISIBLE);


                    }

                }



                holder.list_click.setOnClickListener(view -> {

                    if(holder.list_check.getVisibility() == View.INVISIBLE) {

                        holder.list_check.setVisibility(View.VISIBLE);

                        dm.add(model);
                        Log.d(TAG, "zone:" + dm);


                    } else {

                        holder.list_check.setVisibility(View.INVISIBLE);
                        for( int i = 0; i<dm.size(); i++) {
                            if (dm.get(i).getId().equals(model.getId())) {
                                dm.remove(i);
                            }
                        }
                        Log.d(TAG, "zone:" + dm);
                    }

                });
            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

        mSubmitButton.setOnClickListener(view -> {

            savetoFirestore();
        });
        mremoveDomande.setOnClickListener(view -> {
            removetoFirestore();
        });
    }

    private void removetoFirestore() {
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();

        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(dt.getLuogoID()).collection("Zone").document(dt.getZonaID()).collection("Oggetti").document(dt.getOggettoID()).collection("DomandeTempo").document(dt.getId());
        doc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UpdateTempoDomandeActivity.this, "Domanda a tempo eliminato con successo", Toast.LENGTH_SHORT);
                startActivity(new Intent(UpdateTempoDomandeActivity.this, MyOggettiActivity.class));
            }
        });
    }

    private void savetoFirestore() {

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        String tempoID = tempoDomandeID;
        String nometempo = mnome.getText().toString().trim();
        String tempo = msecondi.getText().toString();
        int secondi =Integer.parseInt(tempo);

        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(dt.getLuogoID()).collection("Zone").document(dt.getZonaID()).collection("Oggetti").document(dt.getOggettoID()).collection("DomandeTempo").document(dt.getId());
        DomandeTempo ndt = new DomandeTempo(dt.getId(),user_id, dt.getLuogoID(), dt.getZonaID(),dt.getOggettoID(),nometempo,secondi,dm);
        doc.set(ndt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UpdateTempoDomandeActivity.this, "Domande a tempo modificata correttamente",Toast.LENGTH_SHORT);
                startActivity(new Intent(UpdateTempoDomandeActivity.this, MyOggettiActivity.class));
            }
        });

    }

    private class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_nome;
        private ImageView list_click;
        private  Button list_check;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_nome=itemView.findViewById(R.id.list_nomeMultipla);

            list_click= itemView.findViewById(R.id.image_multipla_click);
            list_check = itemView.findViewById(R.id.check);


        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

}
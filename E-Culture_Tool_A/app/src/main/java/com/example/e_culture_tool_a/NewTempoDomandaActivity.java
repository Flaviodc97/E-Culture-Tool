package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.UUID;


public class NewTempoDomandaActivity extends AppCompatActivity {
    private final String TAG = "New_Tempo";
    String oggettoID;
    String luogoid;
    String zonaid;
    EditText mnome, msecondi;
    Button mSubmitButton;
    ArrayList<DomandeMultiple> dm = new ArrayList<>();

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String user_id;
    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tempo_domanda);

        // Passaggio della variabili provenienti dall'Intent se esistono
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            oggettoID = extras.getString("oggettoID");
            luogoid = extras.getString("luogoID");
            zonaid = extras.getString("zonaID");
        }

        mnome = findViewById(R.id.NomeDomandaTempo);
        msecondi = findViewById(R.id.UpdateSecondiDomandaTempo);
        mSubmitButton = findViewById(R.id.UpdateSubmitDomandaTempo);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreList=findViewById(R.id.UpdaterecyclerDomandeMUltiple);

        // Query per ottenere le domande multiple per un determinato oggetto
        Query query=fStore.collectionGroup("DomandeMultiple").whereEqualTo("oggettoID", oggettoID);
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
              holder.list_click.setOnClickListener(view -> {

                   if (holder.list_check.getVisibility() == View.INVISIBLE) {

                       holder.list_check.setVisibility(View.VISIBLE);

                       dm.add(model);
                       Log.d(TAG, "zone:" + dm);


                   } else {

                       holder.list_check.setVisibility(View.INVISIBLE);
                       dm.remove(model);
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



    }


    // Salvataggio su Firestore della Domanda a Tempo
    private void savetoFirestore() {
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        String tempoID = usingRandomUUID();
        String nometempo = mnome.getText().toString().trim();
        String tempo = msecondi.getText().toString();
        int secondi =Integer.parseInt(tempo);
        if(TextUtils.isEmpty(nometempo)){
            mnome.setError(getResources().getString(R.string.inserire_un_nome));
            return;
        }
        if(TextUtils.isEmpty(tempo)){
            msecondi.setError(getResources().getString(R.string.inserire_tempo_secondi));
            return;
        }

        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(luogoid).collection("Zone").document(zonaid).collection("Oggetti").document(oggettoID).collection("DomandeTempo").document(tempoID);
        DomandeTempo dt = new DomandeTempo(tempoID,user_id,luogoid,zonaid,oggettoID,nometempo,secondi,dm);
        doc.set(dt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(NewTempoDomandaActivity.this, getResources().getString(R.string.domanda_tempo_ok),Toast.LENGTH_SHORT);
                startActivity(new Intent(NewTempoDomandaActivity.this, MyOggettiActivity.class));
            }
        });
    }

    //Generazione di una Stringa Casuale
    private String usingRandomUUID() {
        UUID randomUUID = UUID.randomUUID();

        return randomUUID.toString().replaceAll("_", "");
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


    public void goQR(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), QRScannerActivity.class));


    }

    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profilo.class));


    }


    public void goHome(View view) {
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        DocumentReference docReference = fStore.collection("utenti").document(user_id);
        docReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                String ruolo = value.getString("Curatore");
                boolean b1 = Boolean.parseBoolean(ruolo);
                if(b1){
                    startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class));

                }else {

                    startActivity(new Intent(getApplicationContext(), HomeVisitatoreActivity.class));
                }


            }
        });
    }


}
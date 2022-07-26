package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_culture_tool_a.Models.DomandeMultiple;
import com.example.e_culture_tool_a.Models.DomandeTempo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.ArrayList;




public class MyTempoDomandeActivity extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String user_id;
    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;
    private String oggettoID;
    String nomeOggetto;
    String luogoid;
    String photoOggetto;
    String zonaid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tempo_domande);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            oggettoID = extras.getString("id");
            nomeOggetto = extras.getString("nome");
            luogoid = extras.getString("luogoID");
            photoOggetto = extras.getString("photo");
            zonaid = extras.getString("zonaID");
        }
            fStore = FirebaseFirestore.getInstance();
            fAuth = FirebaseAuth.getInstance();
            user_id = fAuth.getCurrentUser().getUid();



            mFirestoreList=findViewById(R.id.rectempo);

            // Query per ottenere tutte le domande a Tempo per un determinato oggetto
            Query query=fStore.collectionGroup("DomandeTempo").whereEqualTo("oggettoID", oggettoID);
            FirestoreRecyclerOptions<DomandeTempo> options=new FirestoreRecyclerOptions.Builder<DomandeTempo>().setQuery(query, DomandeTempo.class).build();
            adapter= new FirestoreRecyclerAdapter<DomandeTempo, ProductsViewHolder>(options){
                @NonNull
                @Override
                public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tempodomande_single_item,parent,false);
                    return new ProductsViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull DomandeTempo model) {

                    holder.list_nome.setText(model.getNome());
                    holder.list_click.setOnClickListener(view -> {
                        String id= model.getId();
                        String author = model.getAuthor();
                        String luogoID = model.getLuogoID();
                        String zonaID = model.getZonaID();
                        String oggettoID = model.getOggettoID();
                        String nome = model.getNome();
                        Integer tempo = model.getTempo();
                        ArrayList<DomandeMultiple> dm = new ArrayList<>();
                        dm = model.getDm();
                        Gson gson = new Gson();
                        String myJson = gson.toJson(model);
                        Intent i = new Intent(MyTempoDomandeActivity.this, UpdateTempoDomandeActivity.class);
                        i.putExtra("myjson", myJson);
                        startActivity(i);
                    });

                }
            };

            mFirestoreList.setHasFixedSize(true);
            mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
            mFirestoreList.setAdapter(adapter);





    }


    public void addDomandaMultipla(View view) {

        Intent i = new Intent(MyTempoDomandeActivity.this, NewTempoDomandaActivity.class);
        Bundle args = new Bundle();
        i.putExtra("oggettoID",oggettoID);
        i.putExtra("luogoID",luogoid);
        i.putExtra("zonaID", zonaid);

        startActivity(i);

    }

    private class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_nome;
        private ImageView list_click;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_nome=itemView.findViewById(R.id.list_nomeMultipla);

            list_click= itemView.findViewById(R.id.image_multipla_click);


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
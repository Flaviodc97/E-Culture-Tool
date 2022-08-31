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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_culture_tool_a.Models.DomandeMultiple;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class MyDomandeMultipleActivity extends AppCompatActivity {

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
    TextView nomeOggettoD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_domande_multiple);

        // Vengono prese le Variabile pronvenienti dall'Intent se esistono
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            oggettoID = extras.getString("id");
            nomeOggetto = extras.getString("nome");
            luogoid = extras.getString("luogoID");
            photoOggetto = extras.getString("photo");
            zonaid = extras.getString("zonaID");
        }

        nomeOggettoD = findViewById(R.id.nome_oggetto_domande);
        nomeOggettoD.setText(nomeOggetto);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();

        mFirestoreList=findViewById(R.id.recyclerViewDomande);

        // Query Di tutte le Domande Multiple per L'oggetto selezionato
        Query query=fStore.collectionGroup("DomandeMultiple").whereEqualTo("oggettoID", oggettoID);
        FirestoreRecyclerOptions<DomandeMultiple> options=new FirestoreRecyclerOptions.Builder<DomandeMultiple>().setQuery(query,DomandeMultiple.class).build();
        adapter= new FirestoreRecyclerAdapter<DomandeMultiple, ProductsViewHolder>(options){
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.domandamultipla_single_item,parent,false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull DomandeMultiple model) {



                holder.list_domanda.setText(model.getNome());
                holder.list_click.setOnClickListener(view -> {
                    String id= model.getId();
                    String nome= model.getNome();
                    String rgiusta = model.getRisposta_giusta();
                    List<String> rs = new ArrayList<>();
                    rs = model.getRisposte_errate();
                    Intent i = new Intent(MyDomandeMultipleActivity.this, UpdateDomandeMultipleActivity.class);
                    Bundle args = new Bundle();
                    i.putExtra("id",id);
                    i.putExtra("nome",nome);
                    i.putExtra("rgiusta",rgiusta);
                    i.putExtra("oggettoID", oggettoID);
                    i.putExtra("zonaID", zonaid);
                    i.putExtra("luogoID", luogoid);
                    args.putSerializable("ARRAYLIST",(Serializable)rs);
                    i.putExtra("BUNDLE",args);


                    startActivity(i);
                });

            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);



    }
    // Si viene reinderizzato su AddDomandaActivity
    public void addDomandaMultipla(View view) {
        Intent id = new Intent(MyDomandeMultipleActivity.this, AddDomandaActivity.class);
        id.putExtra("oggettoID",oggettoID);
        id.putExtra("nomeOggetto", nomeOggetto);
        id.putExtra("luogoID", luogoid);
        id.putExtra("photoOggetto", photoOggetto);
        id.putExtra("zonaID", zonaid);
        startActivity(id);


    }


    private class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_domanda;
        private ImageView list_click;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_domanda=itemView.findViewById(R.id.list_nomeMultipla);

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

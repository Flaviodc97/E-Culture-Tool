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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.e_culture_tool_a.Models.Edge;

public class UpdateVisitaActivity extends AppCompatActivity {
    private static final String TAG = "UPDATE_VISITE";
    String idVisita;
    String nomeVisita;
    String author;
    String luogoID;
    EditText mnome;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;
    List<String> zonelist = new ArrayList<>();

    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_visita);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            idVisita = extras.getString("id");
            nomeVisita = extras.getString("nome");
            author = extras.getString("author");
            luogoID = extras.getString("luogoID");
            //The key argument here must match that used in the other activity
        }
        mnome = findViewById(R.id.UpdateNomeVisita);
        mnome.setText(nomeVisita);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreList=(RecyclerView) findViewById(R.id.UpdateEdgeRecycler);
        loadZone();

        Query query=fStore.collectionGroup("Edge").whereEqualTo("visitaID", idVisita);
        FirestoreRecyclerOptions<Edge> options=new FirestoreRecyclerOptions.Builder<Edge>().setQuery(query,Edge.class).build();
        adapter= new FirestoreRecyclerAdapter<Edge,ProductsViewHolder>(options){
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edge_single_item,parent,false);
                return new ProductsViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Edge model) {
                holder.list_zona_inizio.setText(model.getZonaInizio());
                holder.list_zona_fine.setText(model.getZonaFine());
                holder.list_click.setOnClickListener(view -> {
                    String id = model.getId();
                    String zonaInizio = model.getZonaInizio();
                    String zonaFine = model.getZonaFine();
                    String author = model.getAuthor();
                    String visitaID = model.getVisitaID();
                    Intent i = new Intent(UpdateVisitaActivity.this, UpdateEdgeActivity.class);
                    i.putExtra("id",id);
                    i.putExtra("zonaInizio",zonaInizio);
                    i.putExtra("zonaFine",zonaFine);
                    i.putExtra("author",author);
                    i.putExtra("visitaID", visitaID);
                    Bundle arg = new Bundle();
                    arg.putSerializable("ARRAYLIST", (Serializable) zonelist);
                    i.putExtra("BUNDLE", arg);

                    startActivity(i);
                });
            }

        };
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);


    }

    private void loadZone() {
        fStore.collectionGroup("Zone").whereEqualTo("luogoID", luogoID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot document :queryDocumentSnapshots.getDocuments())  {

                    zonelist.add(document.getString("nome"));
                }

                Log.d(TAG," "+zonelist);
            }
        });
    }

    private class ProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView list_zona_inizio;
        private TextView list_zona_fine;
        private ImageView list_click;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_zona_inizio = itemView.findViewById(R.id.zonaInizio);
            list_zona_fine = itemView.findViewById(R.id.zonaFine);
            list_click = itemView.findViewById(R.id.image_click);

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
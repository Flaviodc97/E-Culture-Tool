package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import Models.Visita;
import Models.Zone;

public class MyVisiteActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;
    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_visite);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreList = findViewById(R.id.list_visite);
        Query query = fStore.collection("utenti").document(user_id).collection("Visita");
        FirestoreRecyclerOptions<Visita> options = new FirestoreRecyclerOptions.Builder<Visita>().setQuery(query, Visita.class).build();

        adapter = new FirestoreRecyclerAdapter<Visita, ProductsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Visita model) {
                holder.list_name.setText(model.getNome());
                holder.list_click.setOnClickListener(view -> {

                    String id = model.getId();
                    String nome = model.getNome();
                    String author = model.getAuthor();
                    String luogoID =model.getLuogoID();

                    Intent i = new Intent(MyVisiteActivity.this, UpdateVisitaActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("nome", nome);
                    i.putExtra("author", author);
                    i.putExtra("luogoID", luogoID);
                    startActivity(i);



                });
            }

            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.visita_single_item, parent, false);
                return new ProductsViewHolder(view);
            }

            /*
            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Zone model) {
                holder.list_name.setText(model.getNome());

                holder.list_click.setOnClickListener(view -> {
                    String id = model.getId();
                    String nome = model.getNome();
                    String descrizione = model.getDescrizione();
                    String luogoID = model.getLuogoID();
                    Intent i = new Intent(MyVisiteActivity.this, UpdateVisitaActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("nome", nome);
                    i.putExtra("descrizione", descrizione);
                    i.putExtra("luogoID", luogoID);
                    startActivity(i);

                });
            }*/
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);


    }

    private class ProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView list_name;

        private ImageView list_click;


        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.list_myvisite);
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















    public void addPercorso(View view) {

        startActivity(new Intent(getApplicationContext(), NewVisitaActivity.class));
    }
}
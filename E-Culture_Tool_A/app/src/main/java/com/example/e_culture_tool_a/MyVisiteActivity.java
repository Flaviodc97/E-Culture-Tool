package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_culture_tool_a.Models.Visita;

public class MyVisiteActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;
    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_visite);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreList = findViewById(R.id.list_visite);
        Boolean flag = false;
        Bundle extras = getIntent().getExtras();
        if(extras!=null) flag = extras.getBoolean("flag");
        if(flag){
            query = fStore.collectionGroup("Visita");
        }else{
            query = fStore.collection("utenti").document(user_id).collection("Visita");
        }
        FirestoreRecyclerOptions<Visita> options = new FirestoreRecyclerOptions.Builder<Visita>().setQuery(query, Visita.class).build();

        adapter = new FirestoreRecyclerAdapter<Visita, ProductsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Visita model) {
                holder.list_name.setText(model.getNome());
                holder.list_options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu=new PopupMenu(MyVisiteActivity.this,view);
                        popupMenu.getMenuInflater().inflate(R.menu.visite_menu,popupMenu.getMenu());
                        popupMenu.show();
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.IdAggiornaVisita:
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
                                    break;
                                    case R.id.IdDeleteVisita:
                                        Toast.makeText(MyVisiteActivity.this, "Cancella", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                                return true;
                            }
                        });

                    }
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
        private ImageView list_options;


        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.list_my_visite);
            list_options=itemView.findViewById(R.id.VisiteOptions);

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
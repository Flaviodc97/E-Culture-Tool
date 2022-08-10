package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import Models.Oggetti;


public class MyOggettiActivity extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;

    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_oggetti);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreList=(RecyclerView) findViewById(R.id.firestore_oggetti_list);

        Query query=fStore.collectionGroup("Oggetti").whereEqualTo("author", user_id);
        Toast.makeText(MyOggettiActivity.this," user"+user_id,Toast.LENGTH_SHORT).show();;
        FirestoreRecyclerOptions<Oggetti> options=new FirestoreRecyclerOptions.Builder<Oggetti>().setQuery(query,Oggetti.class).build();

        adapter= new FirestoreRecyclerAdapter<Oggetti, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.oggetti_single_item,parent,false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Oggetti model) {
                holder.list_name.setText(model.getNome());
                holder.list_descrizione.setText(model.getDescrizione()+"");
                if(model.getPhoto()!= null) {
                    Picasso.get().load(model.getPhoto()).into(holder.list_image);
                }



                holder.list_click.setOnClickListener(view -> {
                    String id = model.getId();
                    String nome = model.getNome();
                    String descrizione = model.getDescrizione();
                    String author = model.getAuthor();
                    String luogoID = model.getLuogoID();
                    String zonaID = model.getZonaID();
                    String photo = model.getPhoto();
                    Intent i = new Intent(MyOggettiActivity.this, UpdateOggettiActivity.class);
                    i.putExtra("id",id);
                    i.putExtra("nome",nome);
                    i.putExtra("descrizione",descrizione);
                    i.putExtra("photo",photo);
                    i.putExtra("author", author);
                    i.putExtra("luogoID", luogoID);
                    i.putExtra("zonaID", zonaID);
                    startActivity(i);




                });

                if(model.getPhoto()!= null) {
                    Picasso.get().load(model.getPhoto()).into(holder.list_image);
                }
            }
        };


        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

    }
    private class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_name;
        private TextView list_descrizione;
        private ImageView list_image;
        private ImageView list_click;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name=itemView.findViewById(R.id.list_oggetti);
            list_descrizione=itemView.findViewById(R.id.list_descrizione);
            list_image=itemView.findViewById(R.id.list_oggetti_image);
            list_click = itemView.findViewById(R.id.imageView5);

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


    public void addOggetto(View view) {

        startActivity( new Intent(getApplicationContext(), NewOggettoActivity.class));

    }
    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profile.class));


    }

        /*
    public void goHome(View view) {
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        fstore = FirebaseFirestore.getInstance();
        DocumentReference docReference = fstore.collection("utenti").document(user_id);
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

         */
}
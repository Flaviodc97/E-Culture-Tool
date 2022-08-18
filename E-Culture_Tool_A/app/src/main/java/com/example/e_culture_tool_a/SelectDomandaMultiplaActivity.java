package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_culture_tool_a.Models.DomandeMultiple;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

public class SelectDomandaMultiplaActivity extends AppCompatActivity {

    String idOggetto, nomeOggetto;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;

    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_domanda_multipla);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreList=(RecyclerView) findViewById(R.id.lista_domandem);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idOggetto = extras.getString("id");
            nomeOggetto = extras.getString("nome");
        }

        Query query=fStore.collectionGroup("DomandeMultiple").whereEqualTo("oggettoID", idOggetto);
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

                holder.list_nomeMultipla.setText(model.getNome());
                holder.image_multipla_click.setOnClickListener(view -> {
                    Gson gson = new Gson();
                    String myJson = gson.toJson(model);
                    Intent intent = new Intent(SelectDomandaMultiplaActivity.this, DomandeMultipleActivityV.class);
                    intent.putExtra("myjson", myJson);
                    startActivity(intent);
                });


            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);


    }

    private class ProductsViewHolder extends RecyclerView.ViewHolder{

        private TextView list_nomeMultipla;
        private ImageView image_multipla_click;



        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_nomeMultipla = itemView.findViewById(R.id.list_nomeMultipla);
            image_multipla_click = itemView.findViewById(R.id.image_multipla_click);

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
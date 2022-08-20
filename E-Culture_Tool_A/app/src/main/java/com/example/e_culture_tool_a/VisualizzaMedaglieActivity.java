package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_culture_tool_a.Models.MedaglieDomandeMultiple;
import com.example.e_culture_tool_a.Models.MedaglieDomandeTempo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class VisualizzaMedaglieActivity extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;

    private RecyclerView mFirestoreListMultiple;
    private RecyclerView mFirestoreListTempo;
    private FirestoreRecyclerAdapter adapterMultiple;
    private FirestoreRecyclerAdapter adapterTempo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_medaglie);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreListMultiple=(RecyclerView) findViewById(R.id.rdm);
        mFirestoreListTempo=(RecyclerView) findViewById(R.id.rdt);


        // Query che restituisce le Medaglie per le domande Multiple completate dall'utente
        Query querymultiple=fStore.collection("utenti").document(user_id).collection("MedaglieDomandeMultiple");
        FirestoreRecyclerOptions<MedaglieDomandeMultiple> options=new FirestoreRecyclerOptions.Builder<MedaglieDomandeMultiple>().setQuery(querymultiple,MedaglieDomandeMultiple.class).build();
        // Query che restituisce le Medaglie per le domande a Tempo completate dall'utente
        Query querytempo=fStore.collection("utenti").document(user_id).collection("MedaglieDomandeTempo");
        FirestoreRecyclerOptions<MedaglieDomandeTempo> optionstempo=new FirestoreRecyclerOptions.Builder<MedaglieDomandeTempo>().setQuery(querytempo,MedaglieDomandeTempo.class).build();

        adapterMultiple= new FirestoreRecyclerAdapter<MedaglieDomandeMultiple, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medaglie_multiple_single_item,parent,false);
                return new ProductsViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull MedaglieDomandeMultiple model) {
                holder.list_nome_medaglia.setText(model.getNome());
            }
        };

        adapterTempo= new FirestoreRecyclerAdapter<MedaglieDomandeTempo, ProductsViewHolderTempo>(optionstempo) {
            @NonNull
            @Override
            public ProductsViewHolderTempo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medaglie_tempo_single_item,parent,false);
                return new ProductsViewHolderTempo(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolderTempo holder, int position, @NonNull MedaglieDomandeTempo model) {
                holder.list_nome_medaglia_tempo.setText(model.getNome());
                switch(model.getTipo()){
                    case "Platino": holder.list_image_medaglia_tempo.setBackgroundResource(R.color.Platino);
                        break;
                    case "Oro": holder.list_image_medaglia_tempo.setBackgroundResource(R.color.Oro);
                        break;
                    case "Argento": holder.list_image_medaglia_tempo.setBackgroundResource(R.color.Argento);
                        break;
                    case "Bronzo": holder.list_image_medaglia_tempo.setBackgroundResource(R.color.Bronzo);
                        break;
                }
            }
        };

        mFirestoreListMultiple.setHasFixedSize(true);
        mFirestoreListMultiple.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreListMultiple.setAdapter(adapterMultiple);

        mFirestoreListTempo.setHasFixedSize(true);
        mFirestoreListTempo.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreListTempo.setAdapter(adapterTempo);

    }

    private class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_nome_medaglia;


        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_nome_medaglia=itemView.findViewById(R.id.nome_medaglia_tempo);
        }

    }

    private class ProductsViewHolderTempo extends RecyclerView.ViewHolder{
        private TextView list_nome_medaglia_tempo;
        private ImageView list_image_medaglia_tempo;


        public ProductsViewHolderTempo(@NonNull View itemView) {
            super(itemView);
            list_nome_medaglia_tempo=itemView.findViewById(R.id.nome_medaglia_tempo);
            list_image_medaglia_tempo=itemView.findViewById(R.id.list_medaglie_multiple);
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        adapterMultiple.stopListening();
        adapterTempo.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterMultiple.startListening();
        adapterTempo.startListening();
    }

}
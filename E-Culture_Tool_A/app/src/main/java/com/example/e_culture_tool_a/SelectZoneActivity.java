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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.e_culture_tool_a.Models.Zone;

public class SelectZoneActivity extends AppCompatActivity {
    private static final String TAG = "SELECT_ZONE";
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;
    String luogo_id;
    String nomeVisita;
    String nomeLuogo;

    List<String> zonevisita = new ArrayList<>();
    Button msub;
    Button mavanti;


    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_zone);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreList = findViewById(R.id.firestore_zone_list);
        msub = findViewById(R.id.submitVisita);

        // Variabili ottenuti da Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            luogo_id = extras.getString("id");
            nomeVisita = extras.getString("nomeVisita");
            nomeLuogo = extras.getString("nomeLuogo");


            Log.d(TAG, "zone:" + zonevisita);
            Log.d(TAG, "nomeVisita:" + nomeVisita);



            //The key argument here must match that used in the other activity
        }

        // Query per avere tutte le zone di un luogo
        Query query = fStore.collectionGroup("Zone").whereEqualTo("luogoID", luogo_id);
        FirestoreRecyclerOptions<Zone> options = new FirestoreRecyclerOptions.Builder<Zone>().setQuery(query, Zone.class).build();

        adapter = new FirestoreRecyclerAdapter<Zone, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zone_single_item, parent, false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Zone model) {
                //holder.list_name.setText(model.getNome());
                holder.list_name.setText(model.getNome());
                holder.list_options.setVisibility(View.INVISIBLE);
                holder.list_click.setOnClickListener(view -> {

                    if (holder.list_selezione.getVisibility() == View.INVISIBLE) {

                        holder.list_selezione.setVisibility(View.VISIBLE);



                        zonevisita.add(model.getNome());

                        Log.d(TAG, "zone:" + zonevisita);


                    } else {

                        holder.list_selezione.setVisibility(View.INVISIBLE);
                        zonevisita.remove(model.getNome());
                    }
                });
            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

    // Al click del bottone seleziono le zone da visitare
    msub.setOnClickListener(view -> {
        Intent intent = new Intent(SelectZoneActivity.this, SelectEdgeActivity.class);
        Bundle args = new Bundle();
        intent.putExtra("luogoID", luogo_id);
        intent.putExtra("nomeVisita", nomeVisita);
        intent.putExtra("nomeLuogo", nomeLuogo);
        args.putSerializable("ARRAYLIST",(Serializable)zonevisita);
        intent.putExtra("BUNDLE",args);
        startActivity(intent);

    });



    }
    private class ProductsViewHolder extends RecyclerView.ViewHolder{

        private TextView list_name;
        private ImageView list_options;
        private ImageView list_click;
        private ImageView list_selezione;


        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name=itemView.findViewById(R.id.list_my_zone);
            list_options=itemView.findViewById(R.id.ZoneOptions);

            list_click=itemView.findViewById(R.id.image_click);
            list_selezione = itemView.findViewById(R.id.icon_check);

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

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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Models.Edge;

public class RecapVisitaActivity extends AppCompatActivity {
    private static final String TAG = "RECAP_VISITE" ;
    String visitaID;
    String nomeVisita;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;
    List<String> zonelist = new ArrayList<>();


    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap_visita);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        Bundle args = intent.getBundleExtra("BUNDLE");
        visitaID = extras.getString("idVisita");
        nomeVisita = extras.getString("nomeVisita");
        zonelist = (ArrayList<String>) args.getSerializable("ARRAYLIST");



        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreList=(RecyclerView) findViewById(R.id.item_rec_list);

        Query query=fStore.collectionGroup("Edge").whereEqualTo("visitaID", visitaID);
        FirestoreRecyclerOptions<Edge> options=new FirestoreRecyclerOptions.Builder<Edge>().setQuery(query,Edge.class).build();

        adapter= new FirestoreRecyclerAdapter<Edge, ProductsViewHolder>(options) {
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
                    Intent i = new Intent(RecapVisitaActivity.this, UpdateEdgeActivity.class);
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
    private class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_zona_inizio;
        private TextView list_zona_fine;
        private ImageView list_click;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_zona_inizio=itemView.findViewById(R.id.zonaInizio);
            list_zona_fine=itemView.findViewById(R.id.zonaFine);
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






        /* int i;
        vs = (VerticalStepView) findViewById(R.id.verticalStep);

        DefaultDirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        zonelist = (ArrayList<String>) args.getSerializable("ARRAYLIST");
        Log.d(TAG,"zone:"+zonelist);

        for(i = 0 ; i < zonelist.size() ; i ++){

            graph.addVertex(zonelist.get(i));
        }
        for(i = 0 ; i < zonelist.size()-1 ; i ++){

                graph.addEdge(zonelist.get(i), zonelist.get(i + 1));


        }

        String id = "idtest";
        String nome = "nomepercorsotest";

        JSONObject visita = new JSONObject();
        try {
            visita.put("id", id);
            visita.put("name", nome);
            for(i = 0 ; i < zonelist.size() ; i ++){
                visita.put("zona"+i, zonelist.get(i));
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String jsonStr = visita.toString();
        String filename ="ciao.txt";

        FileOutputStream fo = null;
        try {
            fo = openFileOutput(filename,MODE_PRIVATE);
            fo.write(jsonStr.getBytes());
        } catch (FileNotFoundException e) {


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Log.d(TAG,"zone:"+graph.toString());
        test.add("Prima");
        test.add("Seconda");
        test.add("Terza");
        test.add("Quarta");


        vs.setStepsViewIndicatorComplectingPosition(zonelist.size()-1)
                .reverseDraw(false)
                .setStepViewTexts(zonelist)
                .setLinePaddingProportion(0.85f)
                .setStepsViewIndicatorCompletedLineColor(Color.parseColor("#FFFF00"))
                .setStepViewComplectedTextColor(Color.parseColor("#FFFF00"))
                .setStepViewComplectedTextColor(ContextCompat.getColor(this, com.baoyachi.stepview.R.color.uncompleted_text_color))
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(this, com.baoyachi.stepview.R.color.uncompleted_text_color))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(this, com.baoyachi.stepview.R.color.uncompleted_text_color))
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(this, com.baoyachi.stepview.R.drawable.default_icon))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(this, com.baoyachi.stepview.R.drawable.default_icon))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(this, com.baoyachi.stepview.R.drawable.default_icon));


*/


}
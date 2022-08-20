package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SelectEdgeActivity extends AppCompatActivity {
    private static final String TAG = "SELECT_EDGE";
    private static final String TAGG = "SELECT_EDGE_FIRE";
    List<String> zonelist = new ArrayList<>();
    List<String> zone = new ArrayList<>();
    String luogo_id;
    String nomeVisita;
    String selectedzonainizio;
    String selectedzonafine;
    Spinner mzonaInizio;
    Spinner mzonaFine;
    FirebaseFirestore fStore;
    FirebaseAuth fAth;
    String user_id;
    Integer selected;
    Button mbuttonretry;
    String visitaID;
    Button mavanti;
    String nomeLuogo;
    TextView testo2;

    Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_edge);
        fStore = FirebaseFirestore.getInstance();



        // Variabili ottenuti da Intent
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        Bundle args = intent.getBundleExtra("BUNDLE");
        luogo_id = extras.getString("luogoID");
        nomeVisita = extras.getString("nomeVisita");
        nomeLuogo = extras.getString("nomeLuogo");
        zonelist = (ArrayList<String>) args.getSerializable("ARRAYLIST");
        zonelist.add(0,"Seleziona Zona");
        Log.d(TAG,"zone:"+zonelist);
        Log.d(TAG,"zone:"+luogo_id);





        mzonaInizio = findViewById(R.id.zonaInizioSpinner);
        mzonaFine = findViewById(R.id.zonaFineSpinner);
        mbuttonretry = findViewById(R.id.buttonretry);
        mavanti = findViewById(R.id.buttonAvanti);
        testo2=findViewById(R.id.NameEdge);

        // viene caricato lo spinner di Zone
        loadZone();
        // viene caricato il Grafo
        loadGraph();
        // viene Creata la visita su Firestore
        crateVisita();

        // permette di non far selezionare automaticamente il primo valore negli spinner
        selected = 0;
        ArrayAdapter<String> adapterinizio = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, zone);
        adapterinizio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mzonaInizio.setAdapter(adapterinizio);
        ArrayAdapter<String> adapterfine = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, zone);
        adapterfine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mzonaFine.setAdapter(adapterfine);
        showAlertDialogZonaIniziale(SelectEdgeActivity.this);
        testo2.setText("Inserire la zona di Partenza");


        mzonaInizio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(selected!= 0) {
                    selectedzonainizio = adapterView.getItemAtPosition(i).toString();

                    zone.remove(selectedzonainizio);

                    mzonaInizio.setVisibility(View.INVISIBLE);

                    testo2.setText("Inserire la zona di Arrivo");

                    showAlertDialogZonaFinale(SelectEdgeActivity.this);

                    mzonaFine.setVisibility(View.VISIBLE);

                    adapterfine.notifyDataSetChanged();

                }
                selected++;
                Log.d(TAG, ""+selected);
                Log.d(TAG, "zone "+zone);
                Log.d(TAG, "zonelist"+zonelist);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mzonaFine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, ""+selected);
                Log.d(TAG, "zone "+zone);
                Log.d(TAG, "zonelist"+zonelist);
                if(selected>1) {
                    selectedzonafine = adapterView.getItemAtPosition(i).toString();
                    mzonaFine.setVisibility(View.INVISIBLE);
                    mbuttonretry.setVisibility(View.VISIBLE);
                    mavanti.setVisibility(View.VISIBLE);



                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });








        // Button per inserire nuove Zone per la Creazioni degli edge
        mbuttonretry.setOnClickListener(view -> {
            graph.addEdge(selectedzonainizio, selectedzonafine);
            Log.d(TAG, "GRFO"+graph.toString());
            createEdge();

            onselect(adapterinizio,adapterfine);
        });

        // andiamo avanti nel recapVisiteActivity
        mavanti.setOnClickListener(view -> {
            graph.addEdge(selectedzonainizio, selectedzonafine);
            Log.d(TAG, "GRFO"+graph.toString());
            createEdge();

            Intent i = new Intent(SelectEdgeActivity.this, RecapVisitaActivity.class);
            Bundle arg = new Bundle();
            i.putExtra("idVisita", visitaID);
            i.putExtra("nomeVisita", nomeVisita);
            i.putExtra("luogoID", luogo_id);
            i.putExtra("nomeLuogo", nomeLuogo);
            arg.putSerializable("GRAPH",(Serializable) graph);
            arg.putSerializable("ARRAYLIST", (Serializable) zonelist);

            i.putExtra("BUNDLE", arg);
            startActivity(i);


        });


    }


    public void showAlertDialogZonaIniziale(SelectEdgeActivity view){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Adesso Inserisci la zona di Partenza")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
    }
    public void showAlertDialogZonaFinale(SelectEdgeActivity view){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Adesso Inserisci la zona di Arrivo")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
    }


    // Creazione Edge su Firestore
    private void createEdge() {
        fStore = FirebaseFirestore.getInstance();
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();
        String edgeID =  usingRandomUUID();

        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Visita").document(visitaID).collection("Edge").document(edgeID);
        Map<String, Object> edge = new HashMap<>();
        edge.put("id", edgeID);
        edge.put("zonaInizio", selectedzonainizio);
        edge.put("zonaFine",selectedzonafine);

        edge.put("author", user_id);
        edge.put("visitaID", visitaID);
        doc.set(edge);

    }



    // Creazione visita su Firestore
    private void crateVisita() {
        fStore = FirebaseFirestore.getInstance();
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();
        visitaID = usingRandomUUID();
        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Visita").document(visitaID);
        Map<String, Object> visita = new HashMap<>();
        visita.put("id", visitaID);
        visita.put("nome", nomeVisita);
        visita.put("author",user_id);
        visita.put("luogoID", luogo_id);
        doc.set(visita).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "inserito con successo"+visita);

            }
        });
    }

    //Generazione di una Stringa Casuale
    private String usingRandomUUID() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("_", "");
    }


    // Caricamento dei Vertici(Zone) nel grafo
    private void loadGraph() {
        int i;
        for(i = 1;i < zonelist.size() ; i++)
        {
            graph.addVertex(zonelist.get(i));
        }
        Log.d(TAG,"graph:"+graph.toString());
    }

    // Caricamento della Zonalist
    private void loadZone() {
        int i;
        for(i = 0;i < zonelist.size() ; i++)
        {
            zone.add(zonelist.get(i));


        }
    }


    // quando viene utilizzato button retry si inizializzano gli spinner e la zonalist
    private void onselect(ArrayAdapter<String> adapterinizio, ArrayAdapter<String> adapterfine) {

        zone.clear();
        loadZone();
        selected = 0;

        mzonaInizio.setSelection(0);
        mzonaFine.setSelection(0);

        adapterinizio.notifyDataSetChanged();
        adapterfine.notifyDataSetChanged();
        mzonaInizio.setVisibility(View.VISIBLE);
        mbuttonretry.setVisibility(View.INVISIBLE);
        mavanti.setVisibility(View.INVISIBLE);
        Log.d(TAG, ""+selected);

    }

}
package com.example.e_culture_tool_a;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateEdgeActivity extends AppCompatActivity {
    private static final String TAG = "UPDATE_EDGE";

    String idEdge;
    String zonaInizio;
    String zonaFine;
    String author;
    String visitaID;
    List<String> zonelist = new ArrayList<>();
    List<String> zone = new ArrayList<>();
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;
    Spinner mzonaInizio;
    Spinner mzonaFine;
    Integer selected;
    TextView testo;
    Button mbuttonmodifica;
    String selectedzonainizio;
    String selectedzonafine;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_edge);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        Bundle args = intent.getBundleExtra("BUNDLE");
        idEdge = extras.getString("id");
        zonaInizio= extras.getString("zonaInizio");
        zonaFine = extras.getString("zonaFine");
        author = extras.getString("author");
        visitaID = extras.getString("visitaID");

        zonelist = (ArrayList<String>) args.getSerializable("ARRAYLIST");


        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();

        testo = findViewById(R.id.UpdateTesto);
        mzonaInizio = findViewById(R.id.UpdatezonaInizio);
        mzonaFine = findViewById(R.id.UpdatezonaFine);
        mbuttonmodifica = findViewById(R.id.UpdatebuttonEdge);
        builder=new AlertDialog.Builder(this);

        loadZone();

        selected = 0;
        ArrayAdapter<String> adapterinizio = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, zone);
        adapterinizio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mzonaInizio.setAdapter(adapterinizio);
        ArrayAdapter<String> adapterfine = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, zone);
        adapterfine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mzonaFine.setAdapter(adapterfine);
        showAlertDialogZonaIniziale(UpdateEdgeActivity.this);
        mzonaInizio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(selected!= 0) {
                    selectedzonainizio = adapterView.getItemAtPosition(i).toString();
                    zone.remove(selectedzonainizio);
                    mzonaInizio.setVisibility(View.INVISIBLE);
                    testo.setText("Inserire la zona di Arrivo");
                    showAlertDialogZonaFinale(UpdateEdgeActivity.this);
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
                if(selected!=0) {
                    selectedzonafine = adapterView.getItemAtPosition(i).toString();
                    mzonaFine.setVisibility(View.INVISIBLE);

                    mbuttonmodifica.setVisibility(View.VISIBLE);



                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        mbuttonmodifica.setOnClickListener(view -> {

            updateEdge();
            startActivity(new Intent(UpdateEdgeActivity.this, MyVisiteActivity.class));
            finish();
        });


    }
    public void showAlertDialogZonaIniziale(UpdateEdgeActivity view){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("Adesso Inserisci la zona di Partenza")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
    builder.create().show();
    }
    public void showAlertDialogZonaFinale(UpdateEdgeActivity view){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Adesso Inserisci la zona di Arrivo")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
    }

    private void updateEdge() {
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();


        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Visita").document(visitaID).collection("Edge").document(idEdge);
        Map<String, Object> edge = new HashMap<>();
        edge.put("id", idEdge);
        edge.put("zonaInizio", selectedzonainizio);
        edge.put("zonaFine",selectedzonafine);

        edge.put("author", user_id);
        edge.put("visitaID", visitaID);
        doc.set(edge);

    }

    private void loadZone() {
        int i;
        for(i = 0;i < zonelist.size() ; i++)
        {
            zone.add(zonelist.get(i));


        }
    }
}
package com.example.e_culture_tool_a;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
        zonelist.add(0,"Seleziona una Zona");


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

        // al click item dello spinner
        mzonaInizio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(selected!= 0) {
                    selectedzonainizio = adapterView.getItemAtPosition(i).toString();

                    zone.remove(selectedzonainizio);

                    mzonaInizio.setVisibility(View.INVISIBLE);

                    testo.setText(R.string.zona_arrivo);

                    showAlertDialogZonaFinale(UpdateEdgeActivity.this);

                    mzonaFine.setVisibility(View.VISIBLE);

                    adapterfine.notifyDataSetChanged();

                }
                selected++;



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //al click su item dello spinner
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
            //Update dell'Edge su Firestore
            updateEdge();
            startActivity(new Intent(UpdateEdgeActivity.this, MyVisiteActivity.class));
            finish();
        });


    }
    public void showAlertDialogZonaIniziale(UpdateEdgeActivity view){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage(R.string.zona_partenza_adesso)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
    builder.create().show();
    }
    public void showAlertDialogZonaFinale(UpdateEdgeActivity view){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.zona_arrivo_adesso)
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

    public void goQR(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), QRScannerActivity.class));


    }

    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profilo.class));


    }


    public void goHome(View view) {
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        DocumentReference docReference = fStore.collection("utenti").document(user_id);
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
}
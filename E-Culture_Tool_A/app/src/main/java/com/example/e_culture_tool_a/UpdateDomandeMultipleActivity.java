package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.e_culture_tool_a.Models.DomandeMultiple;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;



public class UpdateDomandeMultipleActivity extends AppCompatActivity {
    EditText mdomanda;
    EditText mrEsatta , mrSbagliata;
    Button mUpdate, mAddSbagliata;
    String domandaID, domanda,rgiusta, oggettoID,zonaID, luogoID;
    ListView mlistSbagliata;
    ArrayList<String>rsbagliata = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_domande_multiple);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        Bundle args = intent.getBundleExtra("BUNDLE");
        domandaID = extras.getString("id");;
        domanda = extras.getString("nome");
        rgiusta = extras.getString("rgiusta");
        rsbagliata = (ArrayList<String>) args.getSerializable("ARRAYLIST");;
        oggettoID = extras.getString("oggettoID");
        zonaID = extras.getString("zonaID");
        luogoID= extras.getString("luogoID");



        mdomanda = findViewById(R.id.domandaUpdate);
        mrEsatta = findViewById(R.id.rEsattaUpdate);
        mUpdate = findViewById(R.id.UpdateDomandeButton);
        mrSbagliata = findViewById(R.id.UpdateSbagliata);
        mAddSbagliata = findViewById(R.id.buttonAddSbagliata);
        mlistSbagliata = (ListView) findViewById(R.id.listSbagliate);
        mdomanda.setText(domanda);
        mrEsatta.setText(rgiusta);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                rsbagliata);

        mlistSbagliata.setAdapter(adapter);
        mlistSbagliata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                removeSbagliata(i, adapter);
            }
        });
        


        mAddSbagliata.setOnClickListener(view -> {

            addNuovarispostaSbagliata(adapter);
        });
        mUpdate.setOnClickListener(view -> {
            if(rsbagliata.size()<3) {
                mrSbagliata.setError(getResources().getString(R.string.numero_minimo_errate));
                return;
            }
            uploadToFirestore();
        });
    }


    // Rimozione di una Risposta Sbagliata

    private void removeSbagliata(int i, ArrayAdapter<String> adapter) {
        rsbagliata.remove(i);
        adapter.notifyDataSetChanged();
    }

    // Caricamento su Firestore della Domanda Multipla Aggiornata
    private void uploadToFirestore() {

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String user_id = fAuth.getCurrentUser().getUid();
        String domanda = mdomanda.getText().toString().trim();
        String giusta = mrEsatta.getText().toString().trim();

        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(luogoID).collection("Zone").document(zonaID).collection("Oggetti").document(oggettoID).collection("DomandeMultiple").document(domandaID);
        DomandeMultiple dm = new DomandeMultiple(domandaID,domanda,giusta,rsbagliata,user_id,luogoID,zonaID,oggettoID);
        doc.set(dm);
        startActivity(new Intent(UpdateDomandeMultipleActivity.this,MyOggettiActivity.class));

    }

    // Inserimento di una nuova risposta Sbagliata
    private void addNuovarispostaSbagliata(ArrayAdapter<String> adapter) {
        rsbagliata.add(mrSbagliata.getText().toString().trim());
        mrSbagliata.setText(" ");
        adapter.notifyDataSetChanged();
    }

    // Rimozione Di una domanda
    public void removeDomanda(View view) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String user_id = fAuth.getCurrentUser().getUid();
        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(luogoID).collection("Zone").document(zonaID).collection("Oggetti").document(oggettoID).collection("DomandeMultiple").document(domandaID);
        doc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UpdateDomandeMultipleActivity.this, getResources().getString(R.string.domanda_multipla_eliminata_ok), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateDomandeMultipleActivity.this,MyOggettiActivity.class));
            }
        });




    }

    public void goQR(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), QRScannerActivity.class));


    }

    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profilo.class));


    }


    public void goHome(View view) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String user_id = fAuth.getCurrentUser().getUid();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
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

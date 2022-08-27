package com.example.e_culture_tool_a;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ShowZone extends AppCompatActivity {

    TextView nomeZona;
    TextView descrZona;

    String nZ;
    String dZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_zone);

        nomeZona = findViewById(R.id.nomeZona);
        descrZona = findViewById(R.id.descrZona);

        // Variabili ottenuti da Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nZ = extras.getString("nomeZona");
            dZ = extras.getString("descrZona");

        }

        //Visualizzo le informazioni relative alle zone
        nomeZona.setText(nZ);
        descrZona.setText(dZ);
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
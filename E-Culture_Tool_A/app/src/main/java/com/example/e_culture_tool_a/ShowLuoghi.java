package com.example.e_culture_tool_a;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_culture_tool_a.Fragment.LuoghiFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class ShowLuoghi extends AppCompatActivity {

    ImageView fotoLuogo;
    TextView nomeLuogo;
    TextView descrLuogo;

    String fL;
    String nL;
    String dL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_luoghi);

        fotoLuogo = findViewById(R.id.fotoLuogo);
        nomeLuogo = findViewById(R.id.nomeLuogo);
        descrLuogo = findViewById(R.id.descrLuogo);

        // Variabili ottenuti da Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fL = extras.getString("fotoLuogo");
            nL = extras.getString("nomeLuogo");
            dL = extras.getString("descrLuogo");

        }

        //Visualizzo le informazioni relative ai luoghi
        if(fL!=null){
            Picasso.get().load(fL).into(fotoLuogo);
        }
        nomeLuogo.setText(nL);
        descrLuogo.setText(dL);
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
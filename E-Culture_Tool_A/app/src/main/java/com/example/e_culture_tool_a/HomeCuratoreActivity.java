package com.example.e_culture_tool_a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HomeCuratoreActivity extends AppCompatActivity {
    ImageView mbuttonVisite;
    ImageView mbuttonLuogo;
    ImageView mbuttonZone;
    ImageView mbuttonOggetti;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mbuttonVisite = findViewById(R.id.viewVisite);
        mbuttonLuogo = findViewById(R.id.viewLuoghiBTN);
        mbuttonZone = findViewById(R.id.viewZoneBTN);
        mbuttonOggetti = findViewById(R.id.viewOggetti);





        //Button Listener

        mbuttonVisite.setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), MyVisiteActivity.class));

        });
        mbuttonLuogo.setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), MyLuoghiActivity.class));

        });
        mbuttonZone.setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), MyZoneActivity.class));

        });

        mbuttonOggetti.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), MyOggettiActivity.class));

        });
    }





    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profile.class));


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
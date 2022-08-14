package com.example.e_culture_tool_a;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeVisitatoreActivity extends AppCompatActivity {
    ImageButton myvisiteButton;
    ImageView visiteCuratoriButton;
    ImageView qrButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_visitatore);
        qrButton = (ImageView)findViewById(R.id.QrImg);
        myvisiteButton = (ImageButton) findViewById(R.id.VisiteVisitatori);
        visiteCuratoriButton = (ImageView)findViewById(R.id.Visiteimg);
        visiteCuratoriButton.setOnClickListener(view -> {

            startActivity( new Intent(getApplicationContext(), MyVisiteActivity.class));

        });
        myvisiteButton.setOnClickListener(view -> {
            startActivity( new Intent(getApplicationContext(), MyVisiteActivity.class));
        });
        qrButton.setOnClickListener( view -> {

            Toast.makeText(HomeVisitatoreActivity.this, "QRActivity Work in progress",Toast.LENGTH_LONG).show();
        });
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
    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profile.class));

    }
}
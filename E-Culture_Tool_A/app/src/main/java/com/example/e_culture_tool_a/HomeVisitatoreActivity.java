package com.example.e_culture_tool_a;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class HomeVisitatoreActivity extends AppCompatActivity {
    ImageButton myvisiteButton;
    ImageView visiteCuratoriButton;
    ImageView qrButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;
    Boolean flag = true;
    ImageView MyVisiteimg;
    TextView visitep;
    TextView miev;
    TextView nomeProfilo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_visitatore);
        qrButton = (ImageView)findViewById(R.id.QrImg);
        myvisiteButton = (ImageButton) findViewById(R.id.VisiteVisitatori);
        visiteCuratoriButton = (ImageView)findViewById(R.id.Visiteimg);
        MyVisiteimg = findViewById(R.id.MyVisiteimg);
        visitep = findViewById(R.id.visitep);
        miev = findViewById(R.id.miev);
        nomeProfilo = findViewById(R.id.nomeProfilo);

        // Controllo se l'utente accede come ospite e imposto le funzioni che può svolgere
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()==null){
            myvisiteButton.setVisibility(View.GONE);
            MyVisiteimg.setVisibility(View.GONE);
            visitep.setVisibility(View.GONE);
            miev.setVisibility(View.GONE);
            nomeProfilo.setText("Home");
        }

        // Se l'utente clicca su Visite curatori viene Reinderizzato in MyvisiteActivity dove visualizzera le visite dei Curatori
        visiteCuratoriButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeVisitatoreActivity.this, MyVisiteActivity.class);
            intent.putExtra("flag", flag);
            startActivity(intent);


        });
        // Se l'utente clicca sulle mie Visite viene reinderizzato in MyvisiteActivity dove visualizza le sue visite
        myvisiteButton.setOnClickListener(view -> {
            startActivity( new Intent(getApplicationContext(), MyVisiteActivity.class));
        });

        // Se l'utente clicca sul QRButton viene reinderizzato in QRScannerActivity
        qrButton.setOnClickListener( view -> {
            Intent intent = new Intent(HomeVisitatoreActivity.this, QRScannerActivity.class);
            intent.putExtra("flag", flag);
            startActivity(intent);
        });
    }
    public void goQR(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), QRScannerActivity.class));


    }

    public void goHome(View view) {
        Toast.makeText(HomeVisitatoreActivity.this, getResources().getString(R.string.ripeti_home), Toast.LENGTH_SHORT).show();

    }
    public void goProfile(MenuItem item) {

        // Controllo se l'utente accede come ospite e lo indirizzo alla schermata iniziale
        if(fAuth.getCurrentUser()==null){
            startActivity( new Intent(getApplicationContext(), MainActivity.class));
        }
        else{
            startActivity( new Intent(getApplicationContext(), Profilo.class));
        }

    }
}
package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_culture_tool_a.Models.MedaglieDomandeMultiple;
import com.example.e_culture_tool_a.Models.MedaglieDomandeTempo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.UUID;

public class FineDomandeActivity extends AppCompatActivity {

    public static final int PUNTEGGIO_MULTIPLE = 50;
    TextView score;
    Button finishQuiz;
    Integer puntit;
    Boolean puntim;
    Integer domandetot;
    Boolean flagm;
    Boolean flagt;
    ImageView medal;
    Double punteggio = 0.0;
    String idMedal;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;

    String idOggetto, nomeOggetto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_domande);

        score = findViewById(R.id.score);
        finishQuiz = findViewById(R.id.finishQuiz);
        medal = findViewById(R.id.medal);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();

        idMedal = usingRandomUUID();


        // Vengono presi le variabili provenienti dall'intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            puntim = extras.getBoolean("puntim");
            puntit = extras.getInt("puntit");
            domandetot = extras.getInt("dimensione");
            flagm = extras.getBoolean("flagm");
            flagt = extras.getBoolean("flagt");
            idOggetto = extras.getString("id");
            nomeOggetto = extras.getString("nome");
        }

        // Viene verificato se si tratta di schermata di fine Gioco del Tipo Domande Multiple
        if(flagm){
            if(puntim){
                score.setText(R.string.risposta_esatta);
                medal.setBackgroundResource(R.color.Oro);
                DocumentReference doc = fStore.collection("utenti").document(user_id).collection("MedaglieDomandeMultiple").document(idMedal);
                MedaglieDomandeMultiple medaglia = new MedaglieDomandeMultiple(idMedal, getResources().getString(R.string.medaglia) +nomeOggetto+ getResources().getString(R.string.oro), PUNTEGGIO_MULTIPLE, idOggetto, user_id);
                doc.set(medaglia).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FineDomandeActivity.this, getResources().getString(R.string.medaglia_assegnata), Toast.LENGTH_SHORT).show();
                    }
                });
                flagm=false;
            }else{
                score.setText(R.string.risposta_sbagliata);
                medal.setVisibility(View.INVISIBLE);
                flagm=false;
            }
        }

        // Viene verificato se si tratta di schermata di fine Gioco del tipo Sfide a Tempo
        if(flagt){
            punteggio = (double) puntit/domandetot;
            punteggio = punteggio * 100;
            String tipo="";
            if(punteggio == 0){
                medal.setVisibility(View.INVISIBLE);
            }
            if(punteggio > 0 && punteggio  <=25) {
                tipo = getResources().getString(R.string.bronzo);
                medal.setBackgroundResource(R.color.Bronzo);
                assegnaMedaglia(tipo);
            }
            if(punteggio > 25 && punteggio <= 75){
                tipo = getResources().getString(R.string.argento);
                medal.setBackgroundResource(R.color.Argento);
                assegnaMedaglia(tipo);
            }
            if(punteggio > 75 && punteggio <= 99){
                tipo = getResources().getString(R.string.oro);
                medal.setBackgroundResource(R.color.Oro);
                assegnaMedaglia(tipo);
            }
            if(punteggio > 99){
                tipo = getResources().getString(R.string.platino);
                medal.setBackgroundResource(R.color.Platino);
                assegnaMedaglia(tipo);
            }
            score.setText(new StringBuilder().append(getResources().getString(R.string.hai_risposto_bene_a)).append(puntit).append(getResources().getString(R.string.domande_su)).append(domandetot).append(getResources().getString(R.string.domande)).append(getResources().getString(R.string.percentuale)).append(punteggio).toString());
            flagt = false;
        }

        finishQuiz.setOnClickListener(view -> {
            Intent intent = new Intent(FineDomandeActivity.this, HomeVisitatoreActivity.class);
            startActivity(intent);
            finish();
        });

    }

    // Creazione di una Stringa casuale
    private String usingRandomUUID() {
        UUID randomUUID = UUID.randomUUID();

        return randomUUID.toString().replaceAll("_", "");
    }


    //Viene assegnata una medagli all'utente che ha completato i game
    private void assegnaMedaglia(String tipo){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        tipo = tipo.trim();
        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("MedaglieDomandeTempo").document(idMedal);
        MedaglieDomandeTempo medaglia = new MedaglieDomandeTempo(idMedal, getResources().getString(R.string.medaglia) +nomeOggetto+ tipo, punteggio, idOggetto, user_id, tipo);
        doc.set(medaglia).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(FineDomandeActivity.this, getResources().getString(R.string.medaglia_assegnata), Toast.LENGTH_SHORT).show();
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
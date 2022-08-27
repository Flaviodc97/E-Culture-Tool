package com.example.e_culture_tool_a;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.e_culture_tool_a.Models.DomandeMultiple;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class DomandeMultipleActivityV extends AppCompatActivity {

    private TextView domanda, result;
    private Button r1, r2, r3, r4, fineQuiz;
    DomandeMultiple dm;
    Map<String, String> map = new LinkedHashMap<>();
    ArrayList<String> rb = new ArrayList<>();
    String rg;
    private final static Integer sizesb = 3;
    Boolean puntim;
    Boolean flagm = true;
    String idOggetto, nomeOggetto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domande_multiple_v);

        // Viene passato nell'intent un Oggetto tramite Gson
        Gson gson = new Gson();
        dm = gson.fromJson(getIntent().getStringExtra("myjson"), DomandeMultiple.class);

        // Vengono prese le variabili passate nell'intent
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            idOggetto = extras.getString("id");
            nomeOggetto = extras.getString("nome");
        }

        domanda = findViewById(R.id.domanda);
        r1 = findViewById(R.id.r1);
        r2 = findViewById(R.id.r2);
        r3 = findViewById(R.id.r3);
        r4 = findViewById(R.id.r4);
        result = findViewById(R.id.result);
        fineQuiz = findViewById(R.id.fineQuiz);
        domanda.setText(dm.getNome());

        //Viene caricata la domanda nella TextView e le risposte nei Button
        loadRisposte();

        // Se l'utente clicca su fine Quiz viene reinderizzato nella schermata di Fine Quiz
        fineQuiz.setOnClickListener(view -> {
            Intent intent = new Intent(DomandeMultipleActivityV.this, FineDomandeActivity.class);
            intent.putExtra("puntim", puntim);
            intent.putExtra("flagm", flagm);
            intent.putExtra("id", idOggetto);
            intent.putExtra("nome", nomeOggetto);
            startActivity(intent);
            finish();
        });

    }

    // Viene caricata la domanda nella TextView e le risposte nei Button
    private void loadRisposte(){

        // Viene presa la risposta Giusta dall'oggetto dm
        rg = dm.getRisposta_giusta();

        // Viene preso l'arrayList delle risposte Errate dall'oggetto dm
        rb = (ArrayList<String>) dm.getRisposte_errate();

        // Viene mischiato l'Arraylist delle risposte Errate
        Collections.shuffle(rb);

        // Viene generato un numero random tra 0  e 3 , essa sara'la posizione della risposta giusta nell'Arraylist
        Random rand = new Random();
        int upperbound = 4;
        int int_random = rand.nextInt(upperbound);

        // Viene inserita la risposta giusta nell'ArrayList nella posizione random trovata casualmente
        rb.add(int_random, rg);

        // Viene inserita ogni domanda nei Button
        for(int i=0; i<sizesb+1; i++){
            switch (i){
                case 0: r1.setText(rb.get(i));
                case 1: r2.setText(rb.get(i));
                case 2: r3.setText(rb.get(i));
                case 3: r4.setText(rb.get(i));
            }
        }
    }


    // Verifica la risposta Cliccata dall'utente
    public void verificaR(View view) {

        // Viene presa la risposta che l'utente ha cliccato
        String answer = ((Button) view).getText().toString().trim();

        // Verifica che la risposta cliccata sia quella giusta
        if(answer.equals(rg)){
            result.setText(R.string.risposta_esatta); // Se risposta giusta
            puntim = true;
        }
        else result.setText(R.string.risposta_sbagliata); //Se risposta sbagliata

        //Viene tolta la possibilita'di cambiare risposta dopo aver cliccato
        r1.setEnabled(false);
        r2.setEnabled(false);
        r3.setEnabled(false);
        r4.setEnabled(false);

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
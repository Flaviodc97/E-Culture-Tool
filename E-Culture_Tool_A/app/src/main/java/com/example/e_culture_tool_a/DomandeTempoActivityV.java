package com.example.e_culture_tool_a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.e_culture_tool_a.Models.DomandeMultiple;
import com.example.e_culture_tool_a.Models.DomandeTempo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DomandeTempoActivityV extends AppCompatActivity {

    private static final String RIGHT = "Risposta Giusta!";
    private static final String WRONG = "Risposta Sbagliata!";
    private TextView domanda, result, time, domandaM;
    private Button r1, r2, r3, r4, homev, next;
    DomandeTempo dt;
    ArrayList<String> rb = new ArrayList<>();
    ArrayList<DomandeMultiple> rm = new ArrayList<>();
    DomandeMultiple dm;
    String rg;
    private final static Integer sizesb = 3;
    CountDownTimer countDownTimer;
    long millisUntilFinished;
    Integer ndomanda;
    Integer dim;
    Integer puntit = 0;
    Boolean flagt = true;
    String idOggetto, nomeOggetto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domande_tempo_v);


        // Passaggio dell'oggetto proveniente dall'Intent attraverso Gson
        Gson gson = new Gson();
        dt = gson.fromJson(getIntent().getStringExtra("myjson"), DomandeTempo.class);

        // Passaggio delle Variabili provenienti dall'Intent
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            idOggetto = extras.getString("id");
            nomeOggetto = extras.getString("nome");
        }

        domanda = findViewById(R.id.domanda);
        domandaM = findViewById(R.id.domandaM);
        r1 = findViewById(R.id.r1);
        r2 = findViewById(R.id.r2);
        r3 = findViewById(R.id.r3);
        r4 = findViewById(R.id.r4);
        result = findViewById(R.id.result);
        homev = findViewById(R.id.fineQuiz);
        domanda.setText(dt.getNome());
        time = findViewById(R.id.time);
        next = findViewById(R.id.next);


        // Se l'utente clicca sul Button next viene mandato alla successiva domanda, se non ci sono piu'domande viene reinderizzato alla schermata di FineDomandaActivity
        next.setOnClickListener(view -> {
            if(ndomanda >=dim){
                Intent intent = new Intent(DomandeTempoActivityV.this, FineDomandeActivity.class);
                intent.putExtra("flagt", flagt);
                intent.putExtra("puntit", puntit);
                intent.putExtra("dimensione", rm.size());
                intent.putExtra("id", idOggetto);
                intent.putExtra("nome", nomeOggetto);
                startActivity(intent);
                finish();
            }else{
                r1.setEnabled(true);
                r2.setEnabled(true);
                r3.setEnabled(true);
                r4.setEnabled(true);
                result.setText("");
                loadRisposte();
            }


        });
        ndomanda = 0;


        //inizializzato il tempo in millisencondi, usando il tempo inserito dal Curatore
        millisUntilFinished = dt.getTempo()*1000;
        loadRisposte();

        //Viene fatto partire il countDown dei secondi
        countDownTimer = new CountDownTimer(millisUntilFinished, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                // Viene mostrato nel setText il tempo che diminuisce ogni secondo
                time.setText("" + (millisUntilFinished / 1000) + "s");
            }
            //Se finisce il tempo senza che l'utente abbia finito di rispondere viene rimandato alla home
            public void onFinish(){
                Intent intent = new Intent(DomandeTempoActivityV.this, HomeVisitatoreActivity.class);
                startActivity(intent);
                countDownTimer = null;
                finish();
            }
        }.start();


        homev.setOnClickListener(view -> {
            Intent intent = new Intent(DomandeTempoActivityV.this, HomeVisitatoreActivity.class);
            startActivity(intent);
        });

    }

    //Viene Caricata la domanda e le risposte della domanda
    private void loadRisposte(){

        // Vengono prese le Domande Multiple dall'oggetto DomandeTempo
        rm = dt.getDm();

        // Viene presa la Domanda Multipla attuale
        dm = rm.get(ndomanda);
        dim = rm.size();
        // Vengono prese le risposte errate della Domanda Multipla Attuale
        rb = (ArrayList<String>) dm.getRisposte_errate();

        // Viene presa la risposta giusta dalla Domanda Multipla Attuale
        rg = dm.getRisposta_giusta();

        // Viene Mischiato l'Arraylist delle risposte sbagliate
        Collections.shuffle(rb);


        domandaM.setText(dm.getNome());

        Random rand = new Random();
        int upperbound = 4;
        //Viene Generato un valore casuale tra 0 e 3 per la posizione della risposta Giusta
        int int_random = rand.nextInt(upperbound);

        //Viene inserita la risposta Giusta nella posizione casuale
        rb.add(int_random, rg);

        //Vengono inserite le risposte nei Button
        for(int i=0; i<sizesb+1; i++){
            switch (i){
                case 0: r1.setText(rb.get(i));
                case 1: r2.setText(rb.get(i));
                case 2: r3.setText(rb.get(i));
                case 3: r4.setText(rb.get(i));
            }
        }
        //viene incrementato il contatore della domanda per passare a una nuova domanda
        ndomanda++;
    }


    //Viene verificata la risposta cliccata dall'utente
    public void verificaR(View view) {
        String answer = ((Button) view).getText().toString().trim();
        if(answer.equals(rg)){
            result.setText(RIGHT);
            puntit++;
        }
        else result.setText(WRONG);
        r1.setEnabled(false);
        r2.setEnabled(false);
        r3.setEnabled(false);
        r4.setEnabled(false);
    }

}
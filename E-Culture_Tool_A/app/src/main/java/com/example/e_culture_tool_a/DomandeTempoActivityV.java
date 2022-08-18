package com.example.e_culture_tool_a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
    Integer prova;
    Integer dim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domande_tempo_v);

        Gson gson = new Gson();
        dt = gson.fromJson(getIntent().getStringExtra("myjson"), DomandeTempo.class);

        domanda = findViewById(R.id.domanda);
        domandaM = findViewById(R.id.domandaM);
        r1 = findViewById(R.id.r1);
        r2 = findViewById(R.id.r2);
        r3 = findViewById(R.id.r3);
        r4 = findViewById(R.id.r4);
        result = findViewById(R.id.result);
        homev = findViewById(R.id.homeV);
        domanda.setText(dt.getNome());
        time = findViewById(R.id.time);
        next = findViewById(R.id.next);

        next.setOnClickListener(view -> {
            if(prova>=dim){
                Intent intent = new Intent(DomandeTempoActivityV.this, HomeVisitatoreActivity.class);
                startActivity(intent);
            }else{
                r1.setEnabled(true);
                r2.setEnabled(true);
                r3.setEnabled(true);
                r4.setEnabled(true);
                loadRisposte();
            }


        });
        prova = 0;

        millisUntilFinished = dt.getTempo()*1000;
        loadRisposte();
        countDownTimer = new CountDownTimer(millisUntilFinished, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                // Update tvTimer every 1 second to show the number of seconds remaining.
                time.setText("" + (millisUntilFinished / 1000) + "s");
            }

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

    private void loadRisposte(){
        rm = dt.getDm();
        dm = rm.get(prova);
        dim = rm.size();
        rb = (ArrayList<String>) dm.getRisposte_errate();
        rg = dm.getRisposta_giusta();
        Collections.shuffle(rb);
        domandaM.setText(dm.getNome());
        Random rand = new Random(); //instance of random class
        int upperbound = 4;
        //generate random values from 0-24
        int int_random = rand.nextInt(upperbound);
        rb.add(int_random, rg);
        for(int i=0; i<sizesb+1; i++){
            switch (i){
                case 0: r1.setText(rb.get(i));
                case 1: r2.setText(rb.get(i));
                case 2: r3.setText(rb.get(i));
                case 3: r4.setText(rb.get(i));
            }
        }
        prova++;
    }

    public void verificaR(View view) {
        String answer = ((Button) view).getText().toString().trim();
        if(answer.equals(rg)) result.setText(RIGHT);
        else result.setText(WRONG);
        r1.setEnabled(false);
        r2.setEnabled(false);
        r3.setEnabled(false);
        r4.setEnabled(false);

    }

}
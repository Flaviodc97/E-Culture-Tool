package com.example.e_culture_tool_a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.e_culture_tool_a.Models.DomandeMultiple;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class DomandeMultipleActivityV extends AppCompatActivity {

    private static final String RIGHT = "Risposta Giusta!";
    private static final String WRONG = "Risposta Sbagliata!";
    private TextView domanda, result;
    private Button r1, r2, r3, r4, homev;
    DomandeMultiple dm;
    Map<String, String> map = new LinkedHashMap<>();
    ArrayList<String> rb = new ArrayList<>();
    String rg;
    private final static Integer sizesb = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domande_multiple_v);

        Gson gson = new Gson();
        dm = gson.fromJson(getIntent().getStringExtra("myjson"), DomandeMultiple.class);

        domanda = findViewById(R.id.domanda);
        r1 = findViewById(R.id.r1);
        r2 = findViewById(R.id.r2);
        r3 = findViewById(R.id.r3);
        r4 = findViewById(R.id.r4);
        result = findViewById(R.id.result);
        homev = findViewById(R.id.homeV);
        domanda.setText(dm.getNome());

        loadRisposte();

        homev.setOnClickListener(view -> {
            Intent intent = new Intent(DomandeMultipleActivityV.this, HomeVisitatoreActivity.class);
            startActivity(intent);
        });

    }

    private void loadRisposte(){
        rg = dm.getRisposta_giusta();
        rb = (ArrayList<String>) dm.getRisposte_errate();
        Random rand = new Random(); //instance of random class
        Collections.shuffle(rb);
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
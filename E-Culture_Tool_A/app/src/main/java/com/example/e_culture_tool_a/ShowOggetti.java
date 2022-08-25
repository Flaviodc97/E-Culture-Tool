package com.example.e_culture_tool_a;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ShowOggetti extends AppCompatActivity {

    ImageView fotoOggetto;
    TextView nomeOggetto;
    TextView descrOggetto;
    TextView idOggetto;

    String idO;
    String fO;
    String nO;
    String dO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_oggetti);

        fotoOggetto = findViewById(R.id.fotoOggetto);
        nomeOggetto = findViewById(R.id.nomeOggetto);
        descrOggetto = findViewById(R.id.descrOggetto);
        idOggetto = findViewById(R.id.idOggetto);

        // Variabili ottenuti da Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idO = extras.getString("idOggetto");
            fO = extras.getString("fotoOggetto");
            nO = extras.getString("nomeOggetto");
            dO = extras.getString("descrOggetto");
        }

        //Visualizzo le informazioni relative agli oggetti
        if(fO!=null){
            Picasso.get().load(fO).into(fotoOggetto);
        }
        idOggetto.setText(getResources().getText(R.string.id_qr_oggetto)+idO);
        nomeOggetto.setText(nO);
        descrOggetto.setText(dO);
    }
}
package com.example.e_culture_tool_a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OggettoView extends AppCompatActivity {

    String idOggetto;
    String nomeOggetto;
    String descrizioneOggetto;
    String photoOggetto;
    String autore;
    String luogoid;
    String zonaid;

    ImageView fotoO;
    TextView nomeO, descrO;
    Button DomM, DomT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oggetto_view);

        fotoO = findViewById(R.id.fotoO);
        nomeO = findViewById(R.id.nomeO);
        descrO = findViewById(R.id.descrO);
        DomM = findViewById(R.id.DomM);
        DomT = findViewById(R.id.DomT);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idOggetto = extras.getString("id");
            nomeOggetto = extras.getString("nome");
            descrizioneOggetto = extras.getString("descrizione");
            photoOggetto = extras.getString("photo");
            autore = extras.getString("author");
            luogoid = extras.getString("luogoID");
            zonaid = extras.getString("zonaID");
        }

        nomeO.setText(nomeOggetto);
        descrO.setText(descrizioneOggetto);
        Picasso.get().load(photoOggetto).into(fotoO);

        DomM.setOnClickListener(view -> {
            Intent intent = new Intent(OggettoView.this, SelectDomandaMultiplaActivity.class);
            intent.putExtra("id", idOggetto);
            intent.putExtra("nome", nomeOggetto);
            startActivity(intent);
        });

        DomT.setOnClickListener(view -> {
            Intent intent = new Intent(OggettoView.this, SelectDomandaTempoActivity.class);
            intent.putExtra("id", idOggetto);
            intent.putExtra("nome", nomeOggetto);
            startActivity(intent);
        });
    }
}
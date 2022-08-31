package com.example.e_culture_tool_a;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

    FirebaseAuth fauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oggetto_view);

        fotoO = findViewById(R.id.fotoO);
        nomeO = findViewById(R.id.nomeO);
        descrO = findViewById(R.id.descrO);
        DomM = findViewById(R.id.DomM);
        DomT = findViewById(R.id.DomT);

        fauth = FirebaseAuth.getInstance();

        // Variabili provenienti dall'intent
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


        // Settaggio delle textView e ImageView con i valori dell'oggetto
        nomeO.setText(nomeOggetto);
        descrO.setText(descrizioneOggetto);
        Picasso.get().load(photoOggetto).into(fotoO);

        if(fauth.getCurrentUser()==null)
        {
            DomM.setVisibility(View.INVISIBLE);
            DomT.setVisibility(View.INVISIBLE);
        }

        // se l'utente clicca su DomandeMultiple
        DomM.setOnClickListener(view -> {
            Intent intent = new Intent(OggettoView.this, SelectDomandaMultiplaActivity.class);
            intent.putExtra("id", idOggetto);
            intent.putExtra("nome", nomeOggetto);
            startActivity(intent);
        });


        //se l'utente clicca su Sfide a Tempo
        DomT.setOnClickListener(view -> {
            Intent intent = new Intent(OggettoView.this, SelectDomandaTempoActivity.class);
            intent.putExtra("id", idOggetto);
            intent.putExtra("nome", nomeOggetto);
            startActivity(intent);
        });
    }



    public void goQR(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), QRScannerActivity.class));


    }

    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profilo.class));


    }


    public void goHome(View view) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        // Controllo se l'utente accede come ospite e lo rimando alla sua home
        if(fAuth.getCurrentUser()==null){
            startActivity(new Intent(getApplicationContext(), HomeVisitatoreActivity.class));
        }
        else{
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
}
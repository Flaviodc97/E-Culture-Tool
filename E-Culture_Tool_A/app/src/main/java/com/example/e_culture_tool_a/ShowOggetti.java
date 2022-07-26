package com.example.e_culture_tool_a;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class ShowOggetti extends AppCompatActivity {

    ImageView fotoOggetto;
    TextView nomeOggetto;
    TextView descrOggetto;
    TextView idOggetto;
    Button copy_Text;

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
        copy_Text = findViewById(R.id.copy_Text);

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

        //Al click del bottone copia l'id dell'oggetto negli appunti del telefono
        copy_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ClipboardManager consiste in una classe con i metodi per ottenere e impostare i dati degli appunti correnti.
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //Con questa istruzione settiamo il testo da copiare
                ClipData clip = ClipData.newPlainText("ID", idO);
                //Inserisco il testo da copiare negli appunti
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ShowOggetti.this, getResources().getString(R.string.id_oggetto_copiato), Toast.LENGTH_SHORT).show();
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
package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import Models.DomandeMultiple;

public class AddDomandaActivity extends AppCompatActivity {
    String oggettoID;
    String nomeOggetto;
    String luogoid;
    String zonaid;
    String photoOggetto;
    List<String> sbagliateList = new ArrayList<>();
    String domanda, rgiusta, domandaID;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;


    EditText mGiusta, mSbagliata, mDomanda;
    TextView mNomeOggetto;
    Button mNewSbagliata, mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_domanda);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            oggettoID = extras.getString("oggettoID");
            nomeOggetto = extras.getString("nomeOggetto");
            luogoid = extras.getString("luogoID");
            photoOggetto = extras.getString("photoOggetto");
            zonaid = extras.getString("zonaID");
        }
        mNomeOggetto = findViewById(R.id.nome_oggetto);
        mNomeOggetto.setText(nomeOggetto);
        mDomanda = findViewById(R.id.Domanda);
        mGiusta = findViewById(R.id.RispostaGiusta);
        mSbagliata = findViewById(R.id.RispostaErrata);
        mNewSbagliata = findViewById(R.id.nuovaRispostaSbagliata);
        mSave = findViewById(R.id.saveButton);

        mNewSbagliata.setOnClickListener(view -> {
            sbagliateList.add(mSbagliata.getText().toString().trim());
            mSbagliata.setText(" ");
        });
        mSave.setOnClickListener(view -> {
            save();
            saveToFirestore();

        });





    }

    private void saveToFirestore() {
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        String user_id = fAuth.getCurrentUser().getUid();
        domandaID = usingRandomUUID();
        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(luogoid).collection("Zone").document(zonaid).collection("Oggetti").document(oggettoID).collection("DomandeMultiple").document(domandaID);
        DomandeMultiple dm = new DomandeMultiple(domandaID, domanda,rgiusta,sbagliateList,user_id,luogoid,zonaid,oggettoID);
        doc.set(dm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AddDomandaActivity.this, "Domanda Inserita con Successo", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddDomandaActivity.this,MyOggettiActivity.class));
            }
        });


    }

    private void save() {
        domanda = mDomanda.getText().toString().trim();
        rgiusta = mGiusta.getText().toString().trim();
        sbagliateList.add(mSbagliata.getText().toString().trim());

    }
    private String usingRandomUUID() {
        UUID randomUUID = UUID.randomUUID();

        return randomUUID.toString().replaceAll("_", "");
    }
}
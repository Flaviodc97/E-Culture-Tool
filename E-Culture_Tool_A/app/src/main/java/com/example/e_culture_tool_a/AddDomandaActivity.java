package com.example.e_culture_tool_a;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_culture_tool_a.Models.DomandeMultiple;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



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

        // Vengono presi i dati  provenienti dall'intent se essi esistono
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


        // Se l'utente clicca su nuova Risposta Sbagliata viene inserita l'attuale risposta Sbagliata nell'Arraylist e l'EditText impostato come vuoto
        mNewSbagliata.setOnClickListener(view -> {
            sbagliateList.add(mSbagliata.getText().toString().trim());
            mSbagliata.setText(" ");
        });

        // Se l'utente clicca su Salva vengono salvati i campi inseriti in Stringhe e viene salvato tutto su Firebase Firestore
        mSave.setOnClickListener(view -> {
            save();

        });





    }

    // Viene salvata la Domanda Multipla su Firebase Firestore
    private void saveToFirestore() {
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        String user_id = fAuth.getCurrentUser().getUid();

        //Viene assegnata una Stringa Casuale come ID della Domanda Multipla
        domandaID = usingRandomUUID();

        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(luogoid).collection("Zone").document(zonaid).collection("Oggetti").document(oggettoID).collection("DomandeMultiple").document(domandaID);
        DomandeMultiple dm = new DomandeMultiple(domandaID, domanda,rgiusta,sbagliateList,user_id,luogoid,zonaid,oggettoID);
        doc.set(dm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AddDomandaActivity.this, R.string.domanda_inserita_ok, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddDomandaActivity.this,MyOggettiActivity.class));
            }
        });


    }


    // Viene salvato in Stringhe il contenuto delle EditText
    private void save() {
        domanda = mDomanda.getText().toString().trim();


        rgiusta = mGiusta.getText().toString().trim();
        if(TextUtils.isEmpty(domanda)){
            mDomanda.setError(getResources().getString(R.string.inserire_una_domanda));
            return;
        }

        if(TextUtils.isEmpty(rgiusta)){
            mGiusta.setError(getResources().getString(R.string.inserire_risposta_giusta));
            return;
        }

        sbagliateList.add(mSbagliata.getText().toString().trim());

        for(int i=0; i <sbagliateList.size(); i++)
        {
            /*if(TextUtils.isEmpty(sbagliateList.get(i))) sbagliateList.remove(i);*/
            if(sbagliateList.get(i).equals("")) sbagliateList.remove(i);
        }

        if(sbagliateList.size()<3){
            mDomanda.setError(getResources().getString(R.string.tre_risposte_errate));
            mGiusta.setError(getResources().getString(R.string.tre_risposte_errate));
            return;
        }
        saveToFirestore();
    }

    //Viene Creata una Stringa casuale
    private String usingRandomUUID() {
        UUID randomUUID = UUID.randomUUID();

        return randomUUID.toString().replaceAll("_", "");
    }
}
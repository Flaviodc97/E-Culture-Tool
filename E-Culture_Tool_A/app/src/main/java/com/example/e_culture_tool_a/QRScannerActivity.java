package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScannerActivity extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);



        // Parte l'intent per lo QRscanner

        new IntentIntegrator(this).initiateScan();
        //Intent intent = new Intent(QRScannerActivity.this,HomeCuratoreActivity.class);
        //startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        // Verifichiamo che l'utente e'un curatore o un visitatore
        DocumentReference docReference = fStore.collection("utenti").document(user_id);
        docReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                String ruolo = value.getString("Curatore");
                boolean b1 = Boolean.parseBoolean(ruolo);
                //otteniamo il risultato dal QRScanner
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                String risultato = result.getContents();
                if(b1){
                    // Se l'utente e'un curatore, Faremo partire searchOggetto()
                    if(risultato!=null){
                        searchOggetto(risultato);
                        finish();
                    }

                }else {

                    // Se l'utente e'un Visitatore, faremo partire quizOggetto()
                    quizOggetto(risultato);

                }


            }
        });

    }



    // Cerchiamo l'oggetto Scannerizzato e facciamo partire l'intent per la modifica (Solo Curatore per i suoi oggetti)
    private void searchOggetto(String risultato) {
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();

        fStore.collectionGroup("Oggetti").whereEqualTo("author", user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (risultato.equals(document.getString("id"))) {
                            String id = document.getId();
                            String nome = document.getString("nome");
                            String descrizione = document.getString("descrizione");
                            String photo = document.getString("photo");
                            String author = document.getString("author");
                            String luogoID = document.getString("luogoID");
                            String zonaID = document.getString("zonaID");

                            Intent i = new Intent(QRScannerActivity.this, UpdateOggettiActivity.class);
                            i.putExtra("id", id);
                            i.putExtra("nome", nome);
                            i.putExtra("descrizione", descrizione);
                            i.putExtra("photo", photo);
                            i.putExtra("author", author);
                            i.putExtra("luogoID", luogoID);
                            i.putExtra("zonaID", zonaID);
                            startActivity(i);
                        }
                    }
                }
            }
        });
    }


    // Cerchiamo l'oggetto Scannerizzato e facciamo partire l'intent per la visulizzazione dei dati dell'oggetto e possibilita di svolgere gli esercizi (Solo Visitatore e Ospite)
    private void quizOggetto(String risultato) {
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();


        fStore.collectionGroup("Oggetti").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (risultato.equals(document.getString("id"))) {
                            String id = document.getId();
                            String nome = document.getString("nome");
                            String descrizione = document.getString("descrizione");
                            String photo = document.getString("photo");
                            String author = document.getString("author");
                            String luogoID = document.getString("luogoID");
                            String zonaID = document.getString("zonaID");



                            Intent i = new Intent(QRScannerActivity.this, OggettoView.class);
                            i.putExtra("id", id);
                            i.putExtra("nome", nome);
                            i.putExtra("descrizione", descrizione);
                            i.putExtra("photo", photo);
                            i.putExtra("author", author);
                            i.putExtra("luogoID", luogoID);
                            i.putExtra("zonaID", zonaID);
                            startActivity(i);
                        }
                    }
                }
            }
        });
    }
}
package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profilo extends AppCompatActivity {

    TextView memail, mnome, mcognome;
    Button mbuttonmodifica, medaglie;
    String curatore;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        memail = findViewById(R.id.editEmail);
        mnome = findViewById(R.id.editNome);
        mcognome = findViewById(R.id.editCognome);
        mbuttonmodifica = findViewById(R.id.buttonModifica);
        medaglie = findViewById(R.id.medaglie);


        // se l'utente e'un curatore viene nascosto il button degli achievement
        isCuratore();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        user_id = fAuth.getCurrentUser().getUid();

        // Caricamento degli attributi dell'utente nelle TexView
        DocumentReference doc = fStore.collection("utenti").document(user_id);
        doc.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                mnome.setText(value.getString("Nome"));
                mcognome.setText(value.getString("Cognome"));
                memail.setText(value.getString("E-mail"));
                curatore = value.getString("Curatore");

            }
        });


        // Se l'utente clicca su modifica profilo
        mbuttonmodifica.setOnClickListener(view -> {
            Intent intent = new Intent(Profilo.this, UpdateProfile.class);
            startActivity(intent);
        });


        // Se l'utente clicca su achievement
        medaglie.setOnClickListener(view -> {
            Intent intent = new Intent(Profilo.this, VisualizzaMedaglieActivity.class);
            startActivity(intent);
        });
    }


    // Nasconde il button achievement se l'utente e'un curatore
    private void isCuratore() {
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        DocumentReference docReference = fStore.collection("utenti").document(user_id);
        docReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                String ruolo = value.getString("Curatore");
                boolean b1 = Boolean.parseBoolean(ruolo);
                if(b1){
                    medaglie.setVisibility(View.INVISIBLE);

                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;


    }

    // Rimuove il profilo da Firebase Firestore e Firebase Authentication
    public void deleteProfile(MenuItem item) {
        FirebaseUser userIstance = fAuth.getCurrentUser();
        AlertDialog.Builder dialog = new AlertDialog.Builder(Profilo.this);
        dialog.setTitle("Eliminazione Account");
        dialog.setMessage("Sicuro di voler eliminare il tuo Account?");
        dialog.setPositiveButton("Elimina Account", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                userIstance.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Profilo.this, "Account eliminato con successo", Toast.LENGTH_LONG).show();
                            DocumentReference docReference = fStore.collection("utenti").document(user_id);
                            docReference.delete();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class ));
                        }else{
                            Toast.makeText(Profilo.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }

                    }
                });

            }
        });
        dialog.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

    }

    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profilo.class));


    }

    public void goHome(View view) {

        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
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

    // Permette all'utente di eseguire il logout
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity( new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
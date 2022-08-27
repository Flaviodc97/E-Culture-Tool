package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {
    EditText memail, mnome, mcognome;
    Button mbuttonmodifica;
    String curatore;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        memail = findViewById(R.id.editEmail);
        mnome = findViewById(R.id.editNome);
        mcognome = findViewById(R.id.editCognome);
        mbuttonmodifica = findViewById(R.id.buttonModifica);






        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        user_id = fAuth.getCurrentUser().getUid();

        //inzializzazione degli EditText con i dati dell'utente
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

        mbuttonmodifica.setOnClickListener(view -> {

            String nome = mnome.getText().toString().trim();
            String cognome = mcognome.getText().toString().trim();
            String email = memail.getText().toString().trim();
            DocumentReference docReference = fStore.collection("utenti").document(user_id);
            Map<String,String> user = new HashMap<>();
            user.put("Nome",nome);
            user.put("Cognome", cognome);
            user.put("E-mail", email);
            user.put("Curatore", curatore);
            docReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("TAG", "Caricato con successo" + user_id);
                    Toast.makeText(UpdateProfile.this, getResources().getString(R.string.profilo_modificato_ok), Toast.LENGTH_SHORT).show();

                }


            });
            Boolean c = Boolean.parseBoolean(curatore);
            if(c){
                startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));
            }else{
                startActivity(new Intent(getApplicationContext(), HomeVisitatoreActivity.class ));
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;


    }


    //possibilita'di eliminare il profilo
    public void deleteProfile(MenuItem item) {
        FirebaseUser userIstance = fAuth.getCurrentUser();
        AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateProfile.this);
        dialog.setTitle(R.string.eliminazione_account);
        dialog.setMessage(R.string.sicuro_elimina_account);
        dialog.setPositiveButton(R.string.elimina_account, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                userIstance.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(UpdateProfile.this, getResources().getString(R.string.account_eliminato_ok), Toast.LENGTH_LONG).show();
                            DocumentReference docReference = fStore.collection("utenti").document(user_id);
                            docReference.delete();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class ));
                        }else{
                            Toast.makeText(UpdateProfile.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

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
    public void goQR(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), QRScannerActivity.class));


    }


    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), UpdateProfile.class));


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
}
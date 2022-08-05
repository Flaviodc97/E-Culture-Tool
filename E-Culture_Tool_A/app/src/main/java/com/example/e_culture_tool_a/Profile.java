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

public class Profile extends AppCompatActivity {
    EditText memail, mnome, mcognome;
    Button mbuttonmodifica;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        memail = findViewById(R.id.editEmail);
        mnome = findViewById(R.id.editNome);
        mcognome = findViewById(R.id.editCognome);
        mbuttonmodifica = findViewById(R.id.buttonModifica);





        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        user_id = fAuth.getCurrentUser().getUid();

        DocumentReference doc = fStore.collection("utenti").document(user_id);
        doc.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
            mnome.setText(value.getString("Nome"));
            mcognome.setText(value.getString("Cognome"));
            memail.setText(value.getString("E-mail"));

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
            docReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("TAG", "Caricato con successo" + user_id);
                    Toast.makeText(Profile.this, "Profilo modificato con successo", Toast.LENGTH_SHORT).show();

                }


            });
            startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;


    }

    public void deleteProfile(MenuItem item) {
        FirebaseUser userIstance = fAuth.getCurrentUser();
        AlertDialog.Builder dialog = new AlertDialog.Builder(Profile.this);
        dialog.setTitle("Eliminazione Account");
        dialog.setMessage("Sicuro di voler eliminare il tuo Account?");
        dialog.setPositiveButton("Elimina Account", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                userIstance.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Profile.this, "Account eliminato con successo", Toast.LENGTH_LONG).show();
                            DocumentReference docReference = fStore.collection("utenti").document(user_id);
                            docReference.delete();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class ));
                        }else{
                            Toast.makeText(Profile.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

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

        startActivity( new Intent(getApplicationContext(), Profile.class));


    }

    public void goHome(View view) {

        startActivity( new Intent(getApplicationContext(), HomeCuratoreActivity.class));

    }
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity( new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
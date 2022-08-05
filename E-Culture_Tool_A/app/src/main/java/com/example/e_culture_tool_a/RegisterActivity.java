package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText memail,mpassword,mrpassword, mnome,mcognome;
    Button mregisterButton;
    ProgressBar mprogressBar;
    FirebaseAuth fAuth;
    Switch mruolo;
    FirebaseFirestore fStore;
    String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        memail = findViewById(R.id.Email);
        mpassword = findViewById(R.id.Password);
        mnome = findViewById(R.id.Nome);
        mcognome = findViewById(R.id.Cognome);
        mrpassword = findViewById(R.id.Rpassword);
        mregisterButton = findViewById(R.id.ButtonRegister);
        mprogressBar = findViewById(R.id.progressBarRegister);
        mruolo =  findViewById(R.id.Ruolo);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();



        if(fAuth.getCurrentUser()!= null){

            startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class));
            finish();
        }


        mregisterButton.setOnClickListener(view -> {

            String email = memail.getText().toString().trim();
            String password = mpassword.getText().toString().trim();
            String nome = mnome.getText().toString().trim();
            String cognome = mcognome.getText().toString().trim();
            Boolean ruolo = mruolo.isChecked();



            if(TextUtils.isEmpty(email)){

                memail.setError("Inserisci l'email");
                return;

            }

            if(TextUtils.isEmpty(password)){

                mpassword.setError("Inserisci la Password");
                return;
            }
            if(TextUtils.isEmpty(nome)){

                mnome.setError("Inserisci Il tuo nome");
                return;
            }
            if(TextUtils.isEmpty(cognome)){

                mcognome.setError("Inserisci il tuo cognome");
                return;
            }

            if(password.length()<8){
                mpassword.setError("la Password deve avere almeno 8 caratteri");
                return;

            }




            mprogressBar.setVisibility(View.VISIBLE);

            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Account Creato con successo", Toast.LENGTH_SHORT).show();
                        user_id = fAuth.getCurrentUser().getUid();
                        DocumentReference docReference = fStore.collection("utenti").document(user_id);
                        String sruolo = String.valueOf(ruolo);
                        Map<String,String> user = new HashMap<>();
                        user.put("Nome",nome);
                        user.put("Cognome", cognome);
                        user.put("E-mail", email);
                        user.put("Curatore", sruolo);
                        docReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG", "Caricato con successo" + user_id);
                            }
                        });
                        mprogressBar.setVisibility(View.GONE);

                        startActivity(new Intent(getApplicationContext(), MainActivity.class ));
                        finish();
                    }else{
                        Toast.makeText(RegisterActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mprogressBar.setVisibility(View.GONE);

                    }


                }
            });



        });

    }
}
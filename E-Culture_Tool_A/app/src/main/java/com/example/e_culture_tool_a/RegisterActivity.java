package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText memail,mpassword,mrpassword, mnome,mcognome;
    Button mregisterButton;
    ProgressBar mprogressBar;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        memail = findViewById(R.id.Email);
        mpassword = findViewById(R.id.Password);
        mnome = findViewById(R.id.Nome);
        mcognome = findViewById(R.id.Cognome);
        mregisterButton = findViewById(R.id.ButtonRegister);
        mprogressBar = findViewById(R.id.progressBarRegister);

        fAuth = FirebaseAuth.getInstance();



        if(fAuth.getCurrentUser()!= null){

            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }


        mregisterButton.setOnClickListener(view -> {

            String email = memail.getText().toString().trim();
            String password = mpassword.getText().toString().trim();

            if(TextUtils.isEmpty(email)){

                memail.setError("Inserisci l'email");
                return;

            }

            if(TextUtils.isEmpty(password)){

                mpassword.setError("Inserisci la Password");
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
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class ));
                    }else{
                        Toast.makeText(RegisterActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mprogressBar.setVisibility(View.INVISIBLE);

                    }


                }
            });



        });

    }
}
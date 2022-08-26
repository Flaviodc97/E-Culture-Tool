package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText memail;
    EditText mpassword;
    Button mloginButton;
    Button mospiteButton;
    TextView mregisterText;
    RadioButton radioItalia, radioInglese;
    ProgressBar mprogressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;
    Locale current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        memail = findViewById(R.id.InputEmail);
        mpassword = findViewById(R.id.InputPassword);
        mloginButton = findViewById(R.id.ButtonRegister);
        mospiteButton = findViewById(R.id.ButtonOspite);
        mregisterText = findViewById(R.id.VaiRegistrazione);
        radioItalia = findViewById(R.id.radioItalia);
        radioInglese = findViewById(R.id.radioInglese);
        mprogressBar = findViewById(R.id.progressBarLogin);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        /* Se l'utente e' già loggato viene reinderizzato alla HomeCuratoreActivity o HomeVisitatoreActivity*/
        if(fAuth.getCurrentUser()!= null){
            redirect();
        }

        /* Se l'utente clicca su crea un account viene renderizzato su RegisterActivity */
        mregisterText.setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

        });
        /* Se l'utente clicca sul button Login vengono testati i valori inseriti nelle EditText e viene effettuato il Login*/
        mloginButton.setOnClickListener(view -> {
            String email = memail.getText().toString().trim();
            String password = mpassword.getText().toString().trim();


            // Se il campo email e' vuoto viene inserito un errore nella EditText
            if(TextUtils.isEmpty(email)){

                memail.setError(getResources().getString(R.string.error_email));
                return;

            }
            // Se il campo password e' vuoto viene inserito un errore nella EditText
            if(TextUtils.isEmpty(password)){

                mpassword.setError(getResources().getString(R.string.error_password));
                return;
            }
            mprogressBar.setVisibility(View.VISIBLE);


            // Login con funzione di Firebase Authentication
            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {



                    if(task.isSuccessful()){
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.login_ok), Toast.LENGTH_SHORT).show();
                        mprogressBar.setVisibility(View.GONE);
                        redirect();

                    }else{
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.error) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mprogressBar.setVisibility(View.GONE);

                    }


                }

            });



        });

        mospiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeVisitatoreActivity.class);
                startActivity(intent);
            }
        });

        current = getResources().getConfiguration().locale;
        if(current.toString().equals("en")) radioInglese.setChecked(true);
        else radioItalia.setChecked(true);

        radioItalia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale current = getResources().getConfiguration().locale;
                if(!current.toString().equals("en") && !current.toString().equals("en_US") && !current.toString().equals("en_us")) {

                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Configuration config = getBaseContext().getResources().getConfiguration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());

                    // Fai rimandare ad homepage!
                    Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(myIntent);
                } else {
                    Locale locale = new Locale("it_IT");
                    Locale.setDefault(locale);
                    Configuration config = getBaseContext().getResources().getConfiguration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());

                    // Fai rimandare ad homepage!
                    Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(myIntent);
                }
            }
        });

        radioInglese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale current = getResources().getConfiguration().locale;
                if(!current.toString().equals("en") && !current.toString().equals("en_US") && !current.toString().equals("en_us")) {

                    Locale locale = new Locale("en");
                    Locale.setDefault(locale);
                    Configuration config = getBaseContext().getResources().getConfiguration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());

                    // Fai rimandare ad homepage!
                    Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(myIntent);
                } else {
                    Locale locale = new Locale("it_IT");
                    Locale.setDefault(locale);
                    Configuration config = getBaseContext().getResources().getConfiguration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());

                    // Fai rimandare ad homepage!
                    Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(myIntent);
                }
            }
        });
    }

    /* Se l'utente e' loggato o effettua il login viene reinderizzato alla HomeCuratoreActivity o alla HomeVisitatoreActivity in base al tipo di Account con cui ci si e' loggati*/
    private void redirect() {
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
package com.example.e_culture_tool_a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class HomeCuratoreActivity extends AppCompatActivity {
    RelativeLayout mLuogo;
    ImageView mbuttonVisite;
    ImageView mbuttonLuogo;
    ImageView mbuttonZone;
    ImageView mbuttonOggetti;
    ImageView mbuttonQR;
    ImageView mbuttonSearch;
    ImageView arrow;
    ScrollView scroll;
    MaterialTapTargetPrompt.Builder builder;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mbuttonVisite = findViewById(R.id.viewVisite);

        mbuttonLuogo = findViewById(R.id.viewLuoghiBTN);
        mLuogo=findViewById(R.id.RelativeLuogo);

        mbuttonZone = findViewById(R.id.viewZoneBTN);
        mbuttonOggetti = findViewById(R.id.viewOggetti);
        mbuttonQR = findViewById(R.id.viewQR);

        mbuttonSearch = findViewById(R.id.viewSearch);
        arrow=findViewById(R.id.imageQuestion);
        scroll=findViewById(R.id.ScrollCuratore);




        // Se Viene Cliccato sul Button viene mostrato un tutoria

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToutorial();
            }
        });


        // Se l'utente clicca sul Button Visite viene reinderizzato in MyVisiteActivity

        mbuttonVisite.setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), MyVisiteActivity.class));

        });

        //Se l'utente clicca sul Button Luoghi viene reinderizzato in MyLuoghiActivity
        mbuttonLuogo.setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), MyLuoghiActivity.class));

        });

        //Se l'utente clicca sul Button Zone viene reinderizzato in MyZonectivity
        mbuttonZone.setOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), MyZoneActivity.class));

        });

        //Se l'utente clicca sul Button Oggetti viene reinderizzato in MyOggettiActivity
        mbuttonOggetti.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), MyOggettiActivity.class));

        });

        //Se l'utente clicca sul Button QR viene reinderizzato in QRScannerActivity
        mbuttonQR.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), QRScannerActivity.class));

        });

        //Se l'utente clicca sul Button Search viene reinderizzato in SearchActivity
        mbuttonSearch.setOnClickListener(view -> {

           startActivity(new Intent(getApplicationContext(), SearchActivity.class));

        });

        /*RICORDA QUANDO ANDREMO A FARE LA PARTE PER TRADURRE BISOGNA USARE LE STRINGHE ANCHE QUI E NON I MESSAGGI NEI DOPPI APICI*/


    }



    public void goQR(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), QRScannerActivity.class));


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

    /* Vengono mostrati i tutorial su come Utilizzare la home page */
    public void showToutorial(){
        showToutorialLuogo();
    }

    public void showToutorialLuogo(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(HomeCuratoreActivity.this)
                .setTarget(R.id.RelativeLuogo)
                .setPrimaryText(R.string.Titolo_luogo)
                .setSecondaryText(R.string.Descrizione_luogo)
                .setPrimaryTextColour(color2)
                .setSecondaryTextColour(color2)
                .setPromptBackground(new RectanglePromptBackground())
                .setBackgroundColour(color1)
                .setPromptFocal(new RectanglePromptFocal())
                .setFocalColour(color2)
                .setCaptureTouchEventOnFocal(true)
                .setCaptureTouchEventOutsidePrompt(true)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FINISHED)
                        {

                            showToutorialZone();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorialZone(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(HomeCuratoreActivity.this)
                .setTarget(R.id.RelativeZone)
                .setPrimaryText(R.string.Titolo_Zona)
                .setSecondaryText(R.string.Descrizione_Zona)
                .setPrimaryTextColour(color2)
                .setSecondaryTextColour(color2)
                .setPromptBackground(new RectanglePromptBackground())
                .setBackgroundColour(color1)
                .setPromptFocal(new RectanglePromptFocal())
                .setFocalColour(color2)
                .setCaptureTouchEventOnFocal(true)
                .setCaptureTouchEventOutsidePrompt(true)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FINISHED)
                        {
                            showToutorialOggetti();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorialOggetti(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(HomeCuratoreActivity.this)
                .setTarget(R.id.RelativeOggetto)
                .setPrimaryText(R.string.Titolo_Oggetto)
                .setSecondaryText(R.string.Descrizione_Oggetto)
                .setPrimaryTextColour(color2)
                .setSecondaryTextColour(color2)
                .setPromptBackground(new RectanglePromptBackground())
                .setBackgroundColour(color1)
                .setPromptFocal(new RectanglePromptFocal())
                .setFocalColour(color2)
                .setCaptureTouchEventOnFocal(true)
                .setCaptureTouchEventOutsidePrompt(true)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FINISHED)
                        {
                            showToutorialVisite();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorialVisite(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(HomeCuratoreActivity.this)
                .setTarget(R.id.RelativeVisite)
                .setPrimaryText(R.string.Titolo_Visite)
                .setSecondaryText(R.string.Descrizione_Visite)
                .setPrimaryTextColour(color2)
                .setSecondaryTextColour(color2)
                .setPromptBackground(new RectanglePromptBackground())
                .setBackgroundColour(color1)
                .setPromptFocal(new RectanglePromptFocal())
                .setFocalColour(color2)
                .setCaptureTouchEventOnFocal(true)
                .setCaptureTouchEventOutsidePrompt(true)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FINISHED)
                        {
                            showToutorialSearch();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorialQr(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(HomeCuratoreActivity.this)
                .setTarget(R.id.RelativeQr)
                .setPrimaryText(R.string.Titolo_Qr)
                .setSecondaryText(R.string.Descrizione_Qr)
                .setPrimaryTextColour(color2)
                .setSecondaryTextColour(color2)
                .setPromptBackground(new RectanglePromptBackground())
                .setBackgroundColour(color1)
                .setPromptFocal(new RectanglePromptFocal())
                .setFocalColour(color2)
                .setCaptureTouchEventOnFocal(true)
                .setCaptureTouchEventOutsidePrompt(true)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FINISHED)
                        {

                        }
                    }
                })
                .show();
        //
    }

    public void showToutorialSearch(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(HomeCuratoreActivity.this)
                .setTarget(R.id.RelativeSearch)
                .setPrimaryText(R.string.Titolo_Search)
                .setSecondaryText(R.string.Descrizione_Search)
                .setPrimaryTextColour(color2)
                .setSecondaryTextColour(color2)
                .setPromptBackground(new RectanglePromptBackground())
                .setBackgroundColour(color1)
                .setPromptFocal(new RectanglePromptFocal())
                .setFocalColour(color2)
                .setCaptureTouchEventOnFocal(true)
                .setCaptureTouchEventOutsidePrompt(true)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FINISHED)
                        {
                            scroll.fullScroll(View.FOCUS_DOWN);
                            showToutorialQr();
                        }
                    }
                })
                .show();
        //
    }

}
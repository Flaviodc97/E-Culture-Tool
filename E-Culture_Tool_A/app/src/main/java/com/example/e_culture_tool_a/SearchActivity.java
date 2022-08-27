package com.example.e_culture_tool_a;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.e_culture_tool_a.Fragment.LuoghiFragment;
import com.example.e_culture_tool_a.Fragment.OggettiFragment;
import com.example.e_culture_tool_a.Fragment.ZoneFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SearchActivity extends AppCompatActivity {
    Button btnLuoghi;
    Button btnZone;
    Button btnOggetti;
    boolean flag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btnLuoghi=findViewById(R.id.SchedaLuoghi);
        btnZone=findViewById(R.id.SchedaZone);
        btnOggetti=findViewById(R.id.SchedaOggetti);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Al click del bottone vengono mostrati tutti i luoghi
        btnLuoghi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.ContainerLuoghi,LuoghiFragment.class,null )
                        .setReorderingAllowed(true)
                        .addToBackStack("name")
                        .commit();
                ChangeBackground(btnLuoghi);
                ResetBackground(btnZone);
                ResetBackground(btnOggetti);

            }

        });

        // Al click del bottone vengono mostrate tutte le zone
        btnZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.ContainerLuoghi,ZoneFragment.class,null )
                        .setReorderingAllowed(true)
                        .addToBackStack("name")
                        .commit();
                flag=false;
                ChangeBackground(btnZone);
                ResetBackground(btnLuoghi);
                ResetBackground(btnOggetti);

            }
        });

        //Al click del bottone vengono mostrati tutti gli oggetti
        btnOggetti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.ContainerLuoghi, OggettiFragment.class,null )
                        .setReorderingAllowed(true)
                        .addToBackStack("name")
                        .commit();
                flag=false;
                ChangeBackground(btnOggetti);
                ResetBackground(btnZone);
                ResetBackground(btnLuoghi);

            }
        });
        /*btnZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.MainContainer,new ZoneFragment()).commit();
            }
        });

         */

    }
    public boolean ChangeBackground(Button button){
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);
        button.setBackgroundColor(color2);
        button.setTextColor(color1);
        return true;
    }

    public void ResetBackground(Button button){
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);
        button.setBackgroundColor(color1);
        button.setTextColor(color2);

    }


    public void goQR(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), QRScannerActivity.class));


    }

    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profilo.class));


    }


    public void goHome(View view) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String user_id = fAuth.getCurrentUser().getUid();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
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
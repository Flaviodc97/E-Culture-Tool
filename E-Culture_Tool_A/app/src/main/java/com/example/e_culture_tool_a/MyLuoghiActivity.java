package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MyLuoghiActivity extends AppCompatActivity {
    public static final String TAG = "Prova";
    TextView mluoghi;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miei_luoghi);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mluoghi = findViewById(R.id.TextLuoghi);

        /* QUery per tutti i Luoghi
        fStore.collectionGroup("Luoghi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
         */

        fStore.collection("utenti").document(user_id).collection("Luoghi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult())  {

                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                }else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


    }

    public void addLuogo(View view) {

        startActivity( new Intent(getApplicationContext(), NewLuogoActivity.class));

    }
    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profile.class));


    }


    public void goHome(View view) {
        startActivity( new Intent(getApplicationContext(), HomeCuratoreActivity.class));
    }
}
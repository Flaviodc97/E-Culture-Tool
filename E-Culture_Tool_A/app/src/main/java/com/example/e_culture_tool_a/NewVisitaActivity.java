package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class NewVisitaActivity extends AppCompatActivity {
    private static final String TAG = "NEW_VISITA";
    Spinner mluogo;
    List<String> luoghi = new ArrayList<>();
    List<String> zone = new ArrayList<>();
    EditText mnomeVisita;
    FirebaseFirestore fStore;
    FirebaseAuth fAth;
    String selectedLuogo;
    String luogo_id;
    String user_id;
    Button msend;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_visita);
        fStore = FirebaseFirestore.getInstance();
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();
        mluogo = findViewById(R.id.spinnerLuogovisite);
        msend = findViewById(R.id.sendluogo);
        mnomeVisita = findViewById(R.id.nomeVisita);



        // adapter Spinner luoghi
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, luoghi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mluogo.setAdapter(adapter);
        fStore.collectionGroup("Luoghi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult())  {
                        //add per Arraylist luoghi
                        luoghi.add(document.getString("nome"));
                    }
                }else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                // notifichiamo all'adapter che e'stato modificato l'arraylist
                adapter.notifyDataSetChanged();
            }
        });

        // Alla selezione di un Item nello spinner luoghi
        mluogo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                // luogo selezionato
                selectedLuogo = parent.getItemAtPosition(position).toString(); //this is your selected item


                //ricerca id del luogo selezionato
                fStore.collectionGroup("Luoghi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if(task.isSuccessful()){
                                    for( QueryDocumentSnapshot document : task.getResult()){
                                        if(selectedLuogo.equals(document.getString("nome"))){
                                            luogo_id = document.getId();
                                            Log.d(TAG, "documents: " +luogo_id);
                                        }

                                    }
                                }
                            }
                        });



            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        // Se l'utente clicca su Invia
        msend.setOnClickListener(view -> {
            String nomeVisita = mnomeVisita.getText().toString().trim();
            Log.d(TAG," "+luogo_id );
            if(TextUtils.isEmpty(nomeVisita)){
                mnomeVisita.setError(getResources().getString(R.string.inserire_un_nome));
                return;
            }
            Intent i = new Intent(NewVisitaActivity.this, SelectZoneActivity.class);
            i.putExtra("nomeLuogo", selectedLuogo);
            i.putExtra("id", luogo_id);
            i.putExtra("nomeVisita", nomeVisita);


            startActivity(i);



        });

    }


    public void goQR(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), QRScannerActivity.class));


    }

    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profilo.class));


    }


    public void goHome(View view) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
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
package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewZoneActivity extends AppCompatActivity {
    public static final String TAG = "Newzone";
    EditText mnome, mdescrizione;
    private Spinner mluogo;
    Button msubmit;
    FirebaseFirestore fStore;
    FirebaseAuth fAth;
    String user_id;
    List<String> luoghi = new ArrayList<>();
    String luogo_id;
    String selectedLuogo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_zone);
        fStore = FirebaseFirestore.getInstance();
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();

        mnome = findViewById(R.id.nomeZona);
        mdescrizione =findViewById(R.id.descrizioneZona);
        mluogo = findViewById(R.id.spinnerLuogo);

        msubmit = findViewById(R.id.inviaOggetto);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, luoghi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mluogo.setAdapter(adapter);
        fStore.collection("utenti").document(user_id).collection("Luoghi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult())  {

                        luoghi.add(document.getString("nome"));
                    }
                }else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                adapter.notifyDataSetChanged();
            }
        });
        mluogo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selectedLuogo = parent.getItemAtPosition(position).toString(); //this is your selected item
                Toast.makeText(NewZoneActivity.this," "+selectedLuogo , Toast.LENGTH_SHORT).show();
                fStore.collection("utenti").document(user_id)
                        .collection("Luoghi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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



        msubmit.setOnClickListener(view -> {
            String nome = mnome.getText().toString().trim();
            String descrizione = mdescrizione.getText().toString().trim();

            Log.d(TAG, "documents: " +luogo_id);
            //Se controllo nome e cognome fare questo
            uploadtoFirestore(nome, descrizione);



        });








    }

    private void uploadtoFirestore(String nome, String descrizione) {
        fStore = FirebaseFirestore.getInstance();
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();
        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(luogo_id).collection("Zone").document();
        Map<String, Object> zona = new HashMap<>();
        zona.put("nome", nome);
        zona.put("descrizione", descrizione);
        zona.put("luogoID", luogo_id);
        zona.put("author", user_id);
        doc.set(zona).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(NewZoneActivity.this,"Zona caricata con successo", Toast.LENGTH_SHORT).show();

            }

        });
        startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));
    }


}
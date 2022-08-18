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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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




        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, luoghi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mluogo.setAdapter(adapter);
        fStore.collectionGroup("Luoghi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                Toast.makeText(NewVisitaActivity.this," "+selectedLuogo , Toast.LENGTH_SHORT).show();
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
        msend.setOnClickListener(view -> {
            String nomeVisita = mnomeVisita.getText().toString().trim();
            Log.d(TAG," "+luogo_id );
            Intent i = new Intent(NewVisitaActivity.this, SelectZoneActivity.class);
            i.putExtra("nomeLuogo", selectedLuogo);
            i.putExtra("id", luogo_id);
            i.putExtra("nomeVisita", nomeVisita);


            startActivity(i);



        });

    }


}
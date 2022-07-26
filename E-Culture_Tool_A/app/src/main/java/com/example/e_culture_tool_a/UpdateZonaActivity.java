package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateZonaActivity extends AppCompatActivity {
    public static final String TAG = "UPDATE_ZONA";
    String id;
    EditText mnome, mdescrizione;
    Spinner mluogo;
    Button mupdateButton, mdelete;
    String user_id;
    List<String> luoghi = new ArrayList<>();
    String luogo_id;
    String selectedLuogo;
    FirebaseFirestore fStore;
    FirebaseAuth fAth;
    String iLuogoid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_zona);
        fStore = FirebaseFirestore.getInstance();
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();


        String inome = " ";
        String idescrizione = " ";


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            inome = extras.getString("nome");
            idescrizione = extras.getString("descrizione");
            iLuogoid = extras.getString("luogoID");
            //The key argument here must match that used in the other activity
        }
        Log.d(TAG, "id luogo: " +iLuogoid+ "id zona"+id);

        mnome = findViewById(R.id.UpdatenomeZona);
        mdescrizione = findViewById(R.id.UpdatedescrizioneZona);
        mluogo = findViewById(R.id.UpdatespinnerLuogo);
        mupdateButton = findViewById(R.id.UpdateOggetto);
        mdelete = findViewById(R.id.deleteZona);

        mnome.setText(inome);
        mdescrizione.setText(idescrizione);
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
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedLuogo = parent.getItemAtPosition(position).toString(); //this is your selected item
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

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mupdateButton.setOnClickListener(view -> {
            String nome = mnome.getText().toString().trim();
            String descrizione = mdescrizione.getText().toString().trim();

            Log.d(TAG, "id luogo: " +iLuogoid+ "id zona"+id);

            // Carico il nome e la descrizione aggiornati sul database
            uploadtoFirestore(nome,descrizione);

            // Al click del bottone elimino la zona dal vecchio luogo di appartenenza
            if((luogo_id.equals(iLuogoid)) == false){
                deletefromFirestore();
            }


        });

        // Al click del bottone elimino una zona
        mdelete.setOnClickListener(view -> {
            deleteZona();
        });

    }

    private void deleteZona() {

        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        user_id = fauth.getCurrentUser().getUid();
        AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateZonaActivity.this);
        dialog.setTitle(R.string.elimina_zona);
        dialog.setMessage(R.string.sicuro_elimina_zona);
        dialog.setPositiveButton(R.string.elimina_zona, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(iLuogoid).collection("Zone").document(id);
                doc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateZonaActivity.this, getResources().getString(R.string.zona_eliminata_ok), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));
                        }
                    }
                });
            }
        });
        dialog.setNegativeButton(R.string.annulla, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void uploadtoFirestore(String nome, String descrizione) {
        fStore = FirebaseFirestore.getInstance();
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();
        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(luogo_id).collection("Zone").document(id);
        Map<String, Object> zona = new HashMap<>();
        zona.put("id", id);
        zona.put("nome", nome);
        zona.put("descrizione", descrizione);
        zona.put("luogoID", luogo_id);
        zona.put("author", user_id);
        doc.set(zona).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UpdateZonaActivity.this, getResources().getString(R.string.zona_modificata_ok), Toast.LENGTH_SHORT).show();

            }

        });
        startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));

    }

    private void deletefromFirestore() {
        fStore = FirebaseFirestore.getInstance();
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();
        Log.d(TAG, "id luogo: " +iLuogoid+ "id zona"+id);
        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(iLuogoid).collection("Zone").document(id);
        doc.delete();
    }

    public void goQR(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), QRScannerActivity.class));


    }

    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profilo.class));


    }


    public void goHome(View view) {
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();
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
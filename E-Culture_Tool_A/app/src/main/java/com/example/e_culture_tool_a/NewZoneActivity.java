package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

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
    ImageView Tutorial;




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
        Tutorial=findViewById(R.id.Question_new_zona);


        // Se l'utente clicca sul button Tutorial
        Tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToutorial();
            }
        });

        // Settiamo l'adapter per lo spinner dei luoghi
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, luoghi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mluogo.setAdapter(adapter);
        fStore.collection("utenti").document(user_id).collection("Luoghi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult())  {
                        // aggiungiamo i luoghi nel'arrayList
                        luoghi.add(document.getString("nome"));
                    }
                }else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                //notifichiamo all'adapter che l'arraylist e' stato modificato
                adapter.notifyDataSetChanged();
            }
        });

        // Al click dell'item nello Spinner
        mluogo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //otteniamo il luogo selezionato
                selectedLuogo = parent.getItemAtPosition(position).toString(); //this is your selected item
                Toast.makeText(NewZoneActivity.this," "+selectedLuogo , Toast.LENGTH_SHORT).show();

                //cerchiamo l'id del luogo selezionato
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



        // se l'utente clicca su Invia
        msubmit.setOnClickListener(view -> {
            String nome = mnome.getText().toString().trim();
            String descrizione = mdescrizione.getText().toString().trim();
            if(TextUtils.isEmpty(nome)){
                mnome.setError(getResources().getString(R.string.inserire_un_nome));
                return;
            }
            if(TextUtils.isEmpty(descrizione)){
                mnome.setError(getResources().getString(R.string.inserire_una_descrizione));
                return;
            }

            Log.d(TAG, "documents: " +luogo_id);

            // upload della zona su Firebase Firestore
            uploadtoFirestore(nome, descrizione);



        });








    }


    //Upload della zona su Firebase FIRESTORE
    private void uploadtoFirestore(String nome, String descrizione) {
        fStore = FirebaseFirestore.getInstance();
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();
        String id = usingRandomUUID();
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
                Toast.makeText(NewZoneActivity.this,R.string.zona_inserita_ok, Toast.LENGTH_SHORT).show();

            }

        });
        startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));
    }

    //Generazione di una Stringa casuale
    private String usingRandomUUID() {
        UUID randomUUID = UUID.randomUUID();

        return randomUUID.toString().replaceAll("_", "");
    }

    public void showToutorial(){
        showToutorial_Nome_New_Zona();
    }

    public void showToutorial_Nome_New_Zona(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(NewZoneActivity.this)
                .setTarget(R.id.nomeZona)
                .setPrimaryText(R.string.Titolo_Nome_New_Zona)
                .setSecondaryText(R.string.Descrizione_Nome_New_Zona)
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
                            showToutorial_Descrizione_New_Zona();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorial_Descrizione_New_Zona(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(NewZoneActivity.this)
                .setTarget(R.id.descrizioneZona)
                .setPrimaryText(R.string.Titolo_Descrizione_New_Zona)
                .setSecondaryText(R.string.Descrizione_Descrizione_New_Zona)
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
                        showToutorial_Spinner_New_Zona();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorial_Spinner_New_Zona(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(NewZoneActivity.this)
                .setTarget(R.id.spinnerLuogo)
                .setPrimaryText(R.string.Titolo_Spinner_New_Zona)
                .setSecondaryText(R.string.Descrizione_Spinner_New_Zona)
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











}
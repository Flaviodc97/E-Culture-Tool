package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class NewOggettoActivity extends AppCompatActivity {
    public static final String TAG = "NEW_OGGETTO";
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    String currentPhotoPath;
    ImageView selectedImage;
    EditText mnome, mdescrizione;
    Spinner mzone;
    Button msubmit, mcamera, mgalleria;
    String user_id;
    List<String> zone = new ArrayList<>();
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    String selectedZona;
    String zona_id;
    String luogo_id;
    String picStorageUrl;
    ImageView Tutorial;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_oggetto);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = fAuth.getCurrentUser().getUid();


        mnome = findViewById(R.id.nomeOggetto);
        mdescrizione = findViewById(R.id.descrizioneOggetto);
        mzone = findViewById(R.id.spinnerZoneOggetto);
        mcamera = findViewById(R.id.cameraOggetto);
        mgalleria = findViewById(R.id.galleriaOggetto);
        selectedImage = findViewById(R.id.imageOggetto);
        Tutorial=findViewById(R.id.Question_new_oggetto);

        //Mostra il tutorial se cliccato
        Tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToutorial();
            }
        });

        //Settiamo adapter per lo spinner zone
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, zone);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mzone.setAdapter(adapter);

        fStore.collectionGroup("Zone").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult())  {
                            if(user_id.equals(document.getString("author"))) {

                                //aggiungiamo zone all'arraylist
                                zone.add(document.getString("nome"));
                            }
                    }
                }else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                //notifichiamo all'adapter dello spinner che la list zona e' stata modificata
                adapter.notifyDataSetChanged();
            }
        });

        // Quando viene selezionato un elemento dello spinner
        mzone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                //viene preso l'elemento selzionato
                selectedZona = parent.getItemAtPosition(i).toString();

                //Cerchiamo l'id della zona selezionata e relativo luogo id
                fStore.collectionGroup("Zone").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if(task.isSuccessful()){
                                    for( QueryDocumentSnapshot document : task.getResult()){
                                        if(selectedZona.equals(document.getString("nome"))){
                                            zona_id = document.getId();
                                            luogo_id = document.getString("luogoID");
                                            Log.d(TAG, "documents: " + zona_id);
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



        msubmit = findViewById(R.id.inviaOggetto);

        //intent per la scatto foto con camera
        mcamera.setOnClickListener(view -> {

            askCamera();
        });

        //intent per galleria
        mgalleria.setOnClickListener(view -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE);




        });
        msubmit.setOnClickListener(view -> {
            String nome = mnome.getText().toString().trim();
            String descrizione = mdescrizione.getText().toString().trim();
            String id = usingRandomUUID();
            if(TextUtils.isEmpty(nome)){
                mnome.setError(getResources().getString(R.string.inserire_un_nome));
                return;
            }
            if(TextUtils.isEmpty(descrizione)){
                mnome.setError(getResources().getString(R.string.inserire_una_descrizione));
                return;
            }


            //Caricamento oggetto su Firebase Firestore
            DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(luogo_id).collection("Zone").document(zona_id).collection("Oggetti").document(id);
            Map<String, Object> oggetto = new HashMap<>();
            oggetto.put("id", id);
            oggetto.put("nome", nome);
            oggetto.put("descrizione", descrizione);
            oggetto.put("photo", picStorageUrl);
            oggetto.put("luogoID", luogo_id);
            oggetto.put("zonaID", zona_id);
            oggetto.put("author", user_id);
            doc.set(oggetto).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(NewOggettoActivity.this,R.string.oggetto_inserito_ok, Toast.LENGTH_SHORT).show();

                }

            });
            startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));


        });



    }

    //Generazione di una Stringa casuale
    private String usingRandomUUID() {
        UUID randomUUID = UUID.randomUUID();

        return randomUUID.toString().replaceAll("_", "");
    }
    //Verifica dei permessi per la Camera
    private void askCamera() {
        //Verifica che sia stata dato il permesso per la Camera
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){

            // Se non si ha i permessi per la Camera vengono chiesti

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
            //Se si ha i permessi per la Camera

            dispatchTakePictureIntent();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                dispatchTakePictureIntent();

            }else{
                // Se rifiutato il permesso della Camera
                Toast.makeText(NewOggettoActivity.this,R.string.richiesta_camera, Toast.LENGTH_SHORT).show();

            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK)
            {
                File f = new File(currentPhotoPath);
                //selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("Uri", "url file is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //Viene preso Uri dal File risultato dello scatto
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                //Viene caricata la foto sullo Storage Firebase
                uploadtoFirebase(f.getName(), contentUri);
            }

        }
        if(requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK)
            {
                //Viene preso l'Uri dalla foto selezionata nella galleria
                Uri contentUri = data.getData();
                // Nome del File appena inserito
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("Uri", "image Uri:" + imageFileName);
                // Viene caricata la foto sullo Storage Firebase
                uploadtoFirebase(imageFileName, contentUri);


            }

        }
    }
    // Viene Caricato su Firebase l'immagine del luogo
    private void uploadtoFirebase(String name, Uri contentUri) {

        StorageReference image = storageReference.child("oggetti/"+ name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("Upload", "onSuccess: " + uri.toString());
                        // Viene caricata l'immagine nella ImageView
                        Picasso.get().load(uri).into(selectedImage);
                        // Viene preso il link alla foto sullo storage
                        picStorageUrl = uri.toString();
                    }

                });
                Toast.makeText(NewOggettoActivity.this,R.string.upload_ok, Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewOggettoActivity.this,R.string.upload_not, Toast.LENGTH_SHORT).show();


            }
        });



    }
    // Ritorna l'estensione dell'immagine
    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));

    }
    // Viene Creato il file con il nome unico
    private File createImageFile() throws IOException {
        // nome del file con nome con la data e ora attuale
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );


        // Salvato il path della foto
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    // Viene Avviata l'intent per scattare una foto
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Verifica se c'e'una camera attiva nel dispositivo

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // File dove andra'la foto scattata
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continua solo se il file e'stato creato con successo

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    public void showToutorial(){
        showToutorial_Camera_New_Oggetto();
    }

    public void showToutorial_Camera_New_Oggetto(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(NewOggettoActivity.this)
                .setTarget(R.id.cameraOggetto)
                .setPrimaryText(R.string.Titolo_Camera_New_Oggetto)
                .setSecondaryText(R.string.Descrizione_Camera_New_Oggetto)
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
                            Toast.makeText(NewOggettoActivity.this, "CANEEEE", Toast.LENGTH_SHORT).show();

                            showToutorial_Galleria_New_Oggetto();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorial_Galleria_New_Oggetto(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(NewOggettoActivity.this)
                .setTarget(R.id.galleriaOggetto)
                .setPrimaryText(R.string.Titolo_Gallery_New_Oggetto)
                .setSecondaryText(R.string.Descrizione_Gallery_New_Oggetto)
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
                            showToutorial_Nome_New_Oggetto();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorial_Nome_New_Oggetto(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(NewOggettoActivity.this)
                .setTarget(R.id.nomeOggetto)
                .setPrimaryText(R.string.Titolo_Nome_New_Oggetto)
                .setSecondaryText(R.string.Descrizione_Nome_New_Oggetto)
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
                            showToutorial_Descrizione_New_Oggetto();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorial_Descrizione_New_Oggetto(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(NewOggettoActivity.this)
                .setTarget(R.id.descrizioneOggetto)
                .setPrimaryText(R.string.Titolo_Descrizione_New_Oggetto)
                .setSecondaryText(R.string.Descrizione_Descrizione_New_Oggetto)
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

        new MaterialTapTargetPrompt.Builder(NewOggettoActivity.this)
                .setTarget(R.id.spinnerZoneOggetto)
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
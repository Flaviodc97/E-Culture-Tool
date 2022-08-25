package com.example.e_culture_tool_a;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;


public class NewLuogoActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;

    ImageView selectedImage;
    Button cameraBtn, galleryBtn, submitButton;
    String currentPhotoPath;
    StorageReference storageReference;
    EditText mnome, mdescrizione;
    FirebaseFirestore fStore;
    FirebaseAuth fAth;
    String user_id;
    String picStorageUrl;

    ImageView Tutorial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_luogo);
        selectedImage = findViewById(R.id.imageView);
        cameraBtn = findViewById(R.id.cameraBtn);
        galleryBtn = findViewById(R.id.galleriaBtn);
        submitButton = findViewById(R.id.SubmitButton);
        mnome = findViewById(R.id.NomeLuogo);
        mdescrizione = findViewById(R.id.DescrizioneLuogo);
        fAth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        Tutorial=findViewById(R.id.Question_new_luogo);

        // Se si clicca sul Button tutorial parte un tutorial per inserire un nuovo luogo
        Tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToutorial();
            }
        });

        //Se si clicca su Camera parte l'intent con apertura della Camera
        cameraBtn.setOnClickListener(view -> {
            askCamera();

        });

        //Se su clicca su Galleria parte l'intent con apertura della galleria
        galleryBtn.setOnClickListener(view -> {
            Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE);

        });
        // Se l'utente clicca su Invia viene Creato il luogo
        submitButton.setOnClickListener( view -> {
            user_id = fAth.getCurrentUser().getUid();
            String nome = mnome.getText().toString().trim();
            String descrizione = mdescrizione.getText().toString();

            //Verifica che il campo nome non sia vuoto
            if(TextUtils.isEmpty(nome)){
                mnome.setError(getResources().getString(R.string.nome_non_vuoto));
                return;
            }

            //Verifica che il campo descrizione non sia vuoto
            if(TextUtils.isEmpty(descrizione)){
                mdescrizione.setError(getResources().getString(R.string.inserire_una_descrizione));
                return;
            }
            String id = usingRandomUUID();
            DocumentReference docReference = fStore.collection("utenti").document(user_id).collection("Luoghi").document(id);
            Map<String, Object> luogo = new HashMap<>();
            luogo.put("id", id);
            luogo.put("nome", nome);
            luogo.put("descrizione", descrizione);
            luogo.put("photo", picStorageUrl);
            luogo.put("author", user_id);
            docReference.set(luogo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(NewLuogoActivity.this, R.string.luogo_inserito_ok, Toast.LENGTH_SHORT).show();
                }



            });

            startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));

        });
    }


    // Generazione di una Stringa Casuale
    private String usingRandomUUID() {
        UUID randomUUID = UUID.randomUUID();

        return randomUUID.toString().replaceAll("_", "");
    }


    //Verifica dei permessi per la Camera
    private void askCamera() {

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
                Toast.makeText(NewLuogoActivity.this,R.string.richiesta_camera, Toast.LENGTH_SHORT).show();

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

        StorageReference image = storageReference.child("luogo/"+ name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Viene caricata l'immagine nella ImageView
                        Picasso.get().load(uri).into(selectedImage);

                        // Viene preso il link alla foto sullo storage
                        picStorageUrl = uri.toString();
                    }

                });
                Toast.makeText(NewLuogoActivity.this,R.string.upload_ok, Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewLuogoActivity.this,R.string.upload_not, Toast.LENGTH_SHORT).show();


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
        showToutorial_Camera_New_Luogo();
    }

    public void showToutorial_Camera_New_Luogo(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(NewLuogoActivity.this)
                .setTarget(R.id.cameraBtn)
                .setPrimaryText(R.string.Titolo_Camera_New_Luogo)
                .setSecondaryText(R.string.Descrizione_Camera_New_Luogo)
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
                            showToutorial_Galleria_New_Luogo();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorial_Galleria_New_Luogo(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(NewLuogoActivity.this)
                .setTarget(R.id.galleriaBtn)
                .setPrimaryText(R.string.Titolo_Gallery_New_Luogo)
                .setSecondaryText(R.string.Descrizione_Gallery_New_Luogo)
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
                        showToutorial_Nome_New_Luogo();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorial_Nome_New_Luogo(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(NewLuogoActivity.this)
                .setTarget(R.id.NomeLuogo)
                .setPrimaryText(R.string.Titolo_Nome_New_Luogo)
                .setSecondaryText(R.string.Descrizione_Nome_New_Luogo)
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
                        showToutorial_Descrizione_New_Luogo();
                        }
                    }
                })
                .show();

    }

    public void showToutorial_Descrizione_New_Luogo(){

        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(NewLuogoActivity.this)
                .setTarget(R.id.DescrizioneLuogo)
                .setPrimaryText(R.string.Titolo_Descrizione_New_Luogo)
                .setSecondaryText(R.string.Descrizione_Descrizione_New_Luogo)
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


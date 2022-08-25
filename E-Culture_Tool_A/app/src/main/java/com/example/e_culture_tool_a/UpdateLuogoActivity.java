package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class UpdateLuogoActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;

    String id;
    EditText mnome, mdescrizione;
    ImageView selectedImage;
    String photourl;
    Button mupdateButton, cameraBtn, galleryBtn, melimina;
    String user_id;
    String currentPhotoPath;
    String picStorageUrl;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_luogo);
        storageReference = FirebaseStorage.getInstance().getReference();

        String inome = " ";
        String idescrizione = " ";
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
             id = extras.getString("id");
             inome = extras.getString("nome");
             idescrizione = extras.getString("descrizione");
             picStorageUrl = extras.getString("photo");

        }

        mnome = findViewById(R.id.updateNomeLuogo);
        mdescrizione = findViewById(R.id.updateDescrizioneLuogo);
        selectedImage = findViewById(R.id.UpdateimageView);
        cameraBtn = findViewById(R.id.cameraUpdateBtn);
        galleryBtn = findViewById(R.id.galleriaUpdateBtn);
        mupdateButton = findViewById(R.id.updateButtonLuogo);
        mnome.setText(inome);
        mdescrizione.setText(idescrizione);
        Picasso.get().load(picStorageUrl).into(selectedImage);
        melimina = findViewById(R.id.deleteLuogo);
        cameraBtn.setOnClickListener(view -> {
            askCamera();
            Toast.makeText(UpdateLuogoActivity.this, "camera click", Toast.LENGTH_SHORT).show();
        });
        galleryBtn.setOnClickListener(view -> {
            Toast.makeText(UpdateLuogoActivity.this,"galleria click", Toast.LENGTH_SHORT).show();
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE);

        });
        mupdateButton.setOnClickListener(view -> {
            String nome = mnome.getText().toString().trim();
            String descrizione = mdescrizione.getText().toString().trim();
            uploadtoFirestore(nome,descrizione);

        });
        melimina.setOnClickListener(view -> {

            deleteLuogo();



        });



    }


// eliminazione del luogo
    public void deleteLuogo() {
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        user_id = fauth.getCurrentUser().getUid();
        AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateLuogoActivity.this);
        dialog.setTitle(R.string.elimina_luogo);
        dialog.setMessage(R.string.sicuro_elimina_luogo);
        dialog.setPositiveButton("Elimina Luogo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(id);
                doc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateLuogoActivity.this, R.string.luogo_eliminato_ok, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));
                        }
                    }
                });
            }
        });
        dialog.setNegativeButton(R.string.annulla_account, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();





    }




 // upload su Firestore del Luogo
    private void uploadtoFirestore(String nome, String descrizione) {

        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        user_id = fauth.getCurrentUser().getUid();
        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(id);
        Map<String, Object> luogo = new HashMap<>();
        luogo.put("id", id);
        luogo.put("nome", nome);
        luogo.put("descrizione", descrizione);
        luogo.put("photo", picStorageUrl);
        luogo.put("author", user_id);


        doc.set(luogo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UpdateLuogoActivity.this,R.string.luogo_modificato_ok, Toast.LENGTH_SHORT).show();
            }
        });
        startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));


    }
    private void askCamera() {
        //Verifica che sia stata dato il permesso per la Camera
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
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
                Toast.makeText(UpdateLuogoActivity.this,R.string.richiesta_camera, Toast.LENGTH_SHORT).show();

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

                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                uploadtoFirebase(f.getName(), contentUri);
            }

        }
        if(requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK)
            {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("Uri", "image Uri:" + imageFileName);
                //selectedImage.setImageURI(contentUri);
                uploadtoFirebase(imageFileName, contentUri);


            }

        }
    }

    private void uploadtoFirebase(String name, Uri contentUri) {

        StorageReference image = storageReference.child("luogo/"+ name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("Upload", "onSuccess: " + uri.toString());
                        Picasso.get().load(uri).into(selectedImage);
                        picStorageUrl = uri.toString();
                    }

                });
                Toast.makeText(UpdateLuogoActivity.this,R.string.upload_ok, Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateLuogoActivity.this,R.string.upload_not, Toast.LENGTH_SHORT).show();


            }
        });



    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {


            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
}
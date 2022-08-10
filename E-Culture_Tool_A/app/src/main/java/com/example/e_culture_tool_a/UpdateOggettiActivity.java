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

public class UpdateOggettiActivity extends AppCompatActivity {
    public static final String TAG = "UPDATE_OGGETTO";
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    String id;
    EditText mnome, mdescrizione;
    Spinner mzona;
    Button mupdateButton, mcamera, mgalleria, mdelete;
    String user_id;
    List<String> zone = new ArrayList<>();
    String zona_id;
    String selectedZona;
    FirebaseFirestore fStore;
    FirebaseAuth fAth;
    String izonaid;
    String picStorageUrl;
    ImageView selectedImage;
    String currentPhotoPath;
    StorageReference storageReference;
    String iLuogoid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_oggetti);

        fStore = FirebaseFirestore.getInstance();
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        String inome = " ";
        String idescrizione = " ";


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            inome = extras.getString("nome");
            idescrizione = extras.getString("descrizione");
            iLuogoid = extras.getString("luogoID");
            picStorageUrl = extras.getString("photo");
            izonaid = extras.getString("zonaID");
        }
        Log.d(TAG,"zona " + izonaid);
            mnome = findViewById(R.id.UpdatenomeOggetto);
            mdescrizione = findViewById(R.id.UpdatedescrizioneOggetto);
            mzona = findViewById(R.id.UpdatespinnerZoneOggetto);
            mupdateButton = findViewById(R.id.UpdateinviaOggetto);
            mcamera = findViewById(R.id.UpdatecameraOggetto);
            mgalleria = findViewById(R.id.UpdategalleriaOggetto);
            selectedImage = findViewById(R.id.UpdateimageOggetto);
            mnome.setText(inome);
            mdelete = findViewById(R.id.deleteOggetto);
            mdescrizione.setText(idescrizione);
            Picasso.get().load(picStorageUrl).into(selectedImage);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, zone);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mzona.setAdapter(adapter);

            fStore.collectionGroup("Zone").whereEqualTo("author", user_id).whereEqualTo("luogoID", iLuogoid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult())  {

                            zone.add(document.getString("nome"));
                        }
                    }else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            mzona.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                    selectedZona = parent.getItemAtPosition(position).toString(); //this is your selected item
                    Toast.makeText(UpdateOggettiActivity.this," "+selectedZona , Toast.LENGTH_SHORT).show();
                    fStore.collectionGroup("Zone").whereEqualTo("author", user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if(task.isSuccessful()){
                                        for( QueryDocumentSnapshot document : task.getResult()){
                                            if(selectedZona.equals(document.getString("nome"))){
                                                zona_id = document.getId();
                                                Log.d(TAG, "documents: " +zona_id);
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

        mcamera.setOnClickListener(view -> {

            askCamera();
        });

        mgalleria.setOnClickListener(view -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE);




        });


           mupdateButton.setOnClickListener( view -> {
               String nome = mnome.getText().toString().trim();
               String descrizione = mdescrizione.getText().toString().trim();

               Log.d(TAG, " id luogo "+iLuogoid+" id Zona"+ zona_id);

               uploadtoFirestore(nome, descrizione);
               if((zona_id.equals(izonaid)) == false){
                   deletefromFirestore();
               }



           });
           mdelete.setOnClickListener(view -> {

               deleteOggetto();
           });

        }

    private void deleteOggetto() {

        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        user_id = fauth.getCurrentUser().getUid();
        AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateOggettiActivity.this);
        dialog.setTitle("Elimina Oggetto");
        dialog.setMessage("Sicuro di voler eliminare l'oggetto selezionato?");
        dialog.setPositiveButton("Elimina Oggetto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG,"luogo "+iLuogoid+ " zona "+ izonaid+" oggetto"+id);
                DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(iLuogoid).collection("Zone").document(izonaid).collection("Oggetti").document(id);
                doc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateOggettiActivity.this, "Oggetto eliminato con successo", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));
                        }
                    }
                });
            }
        });
        dialog.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

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
                Toast.makeText(UpdateOggettiActivity.this,"Bisogna Garantire il Permesso per la Camera", Toast.LENGTH_SHORT).show();

            }
        }

    }
    //intent per aprire la camera
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

        StorageReference image = storageReference.child("oggetti/"+ name);
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
                Toast.makeText(UpdateOggettiActivity.this,"Upload con successo", Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateOggettiActivity.this,"ERROR UPLOAD non andato a buon fine", Toast.LENGTH_SHORT).show();


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
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }



    private void uploadtoFirestore(String nome, String descrizione) {
        fStore = FirebaseFirestore.getInstance();
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();
        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(iLuogoid).collection("Zone").document(zona_id).collection("Oggetti").document(id);
        Map<String, Object> oggetto = new HashMap<>();
        oggetto.put("id", id);
        oggetto.put("nome", nome);
        oggetto.put("descrizione", descrizione);
        oggetto.put("photo", picStorageUrl);
        oggetto.put("zonaID", zona_id);
        oggetto.put("luogoID", iLuogoid);
        oggetto.put("author", user_id);
        doc.set(oggetto).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UpdateOggettiActivity.this,"Oggetto modificato con successo", Toast.LENGTH_SHORT).show();

            }

        });
        startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));



    }

    private void deletefromFirestore() {
        fStore = FirebaseFirestore.getInstance();
        fAth = FirebaseAuth.getInstance();
        user_id = fAth.getCurrentUser().getUid();
        Log.d(TAG, "id luogo: " +iLuogoid+ "id zona"+izonaid +"id oggetto"+id);
         DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(iLuogoid).collection("Zone").document(izonaid).collection("Oggetti").document(id);
        doc.delete();
    }
}
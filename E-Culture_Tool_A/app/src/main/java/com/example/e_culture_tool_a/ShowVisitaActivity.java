package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_culture_tool_a.Models.DomandeTempo;
import com.example.e_culture_tool_a.Models.Edge;
import com.example.e_culture_tool_a.Models.Visita;
import com.example.e_culture_tool_a.Models.Zone;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class ShowVisitaActivity extends AppCompatActivity {
    private final int STORAGE_PERMISSION_CODE = 1;
    private static final String SHARE = "_SHARE";
    private static final String SHARE_FOLDER = "fileShare";
    private Uri contentUri;
    private Visita visita;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    private TextView mnomeVisita;
    Button mshare, msave;


    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_visita);

        Gson gson = new Gson();
        visita = gson.fromJson(getIntent().getStringExtra("myjson"), Visita.class);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        mFirestoreList=findViewById(R.id.EdgeVisitaShow);

        mnomeVisita = findViewById(R.id.nomeVisitaShow);
        mshare = findViewById(R.id.ShowshareButton);
        msave = findViewById(R.id.ShowsalvaButton);


        // Salvataggio del FIle sul dispositivo
        msave.setOnClickListener(view -> {


            salvaFile();

        });

        //Codivisione del file via Bluetooth Gmail ecc.
        mshare.setOnClickListener(view -> {



            shareMessage();

        });
            mnomeVisita.setText(visita.getNome());

        Query query=fStore.collectionGroup("Edge").whereEqualTo("visitaID", visita.getId());
        FirestoreRecyclerOptions<Edge> options=new FirestoreRecyclerOptions.Builder<Edge>().setQuery(query,Edge.class).build();
        adapter= new FirestoreRecyclerAdapter<Edge, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edge_single_item,parent,false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Edge model) {
               holder.list_zonaInizio.setText(model.getZonaInizio());
               holder.list_zonaFine.setText(model.getZonaFine());

            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);


    }

    private void shareMessage() {

        salvaFile();

        String stringFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" + visita.getFile();

        File file = new File(stringFile);
        contentUri = FileProvider.getUriForFile(ShowVisitaActivity.this, "com.example.android.fileprovider", file);

        //Controlla se il file esiste
        if(!file.exists()){
            Toast.makeText(ShowVisitaActivity.this, getResources().getString(R.string.file_non_esiste), Toast.LENGTH_LONG).show();
            return;
        }

        //Attributi che l'intent deve avere per poter effettuare lo share e il richiamo di applicazioni che consentono di ricevere file
        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("text/*");
        intentShare.putExtra(Intent.EXTRA_SUBJECT, "Subject Here"); //per condividere con email app
        intentShare.putExtra(Intent.EXTRA_STREAM, contentUri);
        intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ShowVisitaActivity.this.startActivity(Intent.createChooser(intentShare, String.valueOf(R.string.condividi_file)));


    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //mostro spiegazione del permesso richeisto
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permnessi_salva_file)
                    .setMessage(R.string.accetta)
                    .setPositiveButton(R.string.accetta, (dialogInterface, i) -> ActivityCompat.requestPermissions(
                            ShowVisitaActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton(R.string.annulla,
                            (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("string");
                builder.setPositiveButton("ivnrivm", (dialog, which) -> { });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }
    public boolean checkPermission(String permessi) {
        int check = ContextCompat.checkSelfPermission(this,permessi);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
    private void salvaFile() {

        if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            // Salva in: /storage/emulated/0/Downloads.
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    visita.getFile()); // tolgo .json dal nome

            try {
                FileOutputStream f = new FileOutputStream(file);
                f.write(visita.getMessage().getBytes(StandardCharsets.UTF_8));
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, getResources().getString(R.string.file_salvato), Toast.LENGTH_LONG).show();
        } else {
            requestPermissions();
        }





    }

    private class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_zonaInizio;
        private TextView list_zonaFine;



        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_zonaInizio = itemView.findViewById(R.id.zonaInizio);
            list_zonaFine = itemView.findViewById(R.id.zonaFine);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }


    public void goQR(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), QRScannerActivity.class));


    }

    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profilo.class));


    }


    public void goHome(View view) {
        fAuth = FirebaseAuth.getInstance();
        String user_id = fAuth.getCurrentUser().getUid();
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

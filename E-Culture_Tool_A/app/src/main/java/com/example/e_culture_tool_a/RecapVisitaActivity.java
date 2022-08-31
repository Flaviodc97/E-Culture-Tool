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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.example.e_culture_tool_a.Models.Edge;

public class RecapVisitaActivity extends AppCompatActivity {
    private static final String TAG = "RECAP_VISITE" ;
    private final int STORAGE_PERMISSION_CODE = 1;
    private static final String SHARE = "_SHARE";
    private static final String SHARE_FOLDER = "fileShare";
    private Uri contentUri;
    String visitaID;
    String nomeVisita;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;
    String luogo_id;
    String nomeLuogo;
    String filename ="Visita_";
    String ext=".txt";
    Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

    List<String> zonelist = new ArrayList<>();
    Button mshare, msave, returnHome;
    String message;



    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap_visita);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        Bundle args = intent.getBundleExtra("BUNDLE");
        visitaID = extras.getString("idVisita");
        nomeVisita = extras.getString("nomeVisita");
        luogo_id = extras.getString("luogoID");
        nomeLuogo = extras.getString("nomeLuogo");
        graph = (Graph<String, DefaultEdge>) args.getSerializable("GRAPH");
        zonelist = (ArrayList<String>) args.getSerializable("ARRAYLIST");



        Log.d(TAG,luogo_id);

        mshare = findViewById(R.id.shareButton);
        msave = findViewById(R.id.salvaButton);
        returnHome = findViewById(R.id.returnHome);



        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreList=(RecyclerView) findViewById(R.id.item_rec_list);


        // Query per trovare tutti gli edge di una Visita specifica
        Query query=fStore.collectionGroup("Edge").whereEqualTo("visitaID", visitaID);
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
                holder.list_zona_inizio.setText(model.getZonaInizio());
                holder.list_zona_fine.setText(model.getZonaFine());
                holder.list_click.setOnClickListener(view -> {
                    String id = model.getId();
                    String zonaInizio = model.getZonaInizio();
                    String zonaFine = model.getZonaFine();
                    String author = model.getAuthor();
                    String visitaID = model.getVisitaID();
                    Intent i = new Intent(RecapVisitaActivity.this, UpdateEdgeActivity.class);
                    i.putExtra("id",id);
                    i.putExtra("zonaInizio",zonaInizio);
                    i.putExtra("zonaFine",zonaFine);
                    i.putExtra("author",author);
                    i.putExtra("visitaID", visitaID);
                    Bundle arg = new Bundle();
                    arg.putSerializable("ARRAYLIST", (Serializable) zonelist);
                    i.putExtra("BUNDLE", arg);

                    startActivity(i);
                });
            }
        };


        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);
        // Carichiamo il messaggio da usare nel file
        loadMessage();
        UpdateToFirestore();



        // Se l'utente clicca su salva visita
    msave.setOnClickListener(view -> {

        // salvataggio del file sul telefono
        salvaFile();


    });
    mshare.setOnClickListener(view -> {


        // condivisione del file
            shareMessage();


        });

    returnHome.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            fAuth = FirebaseAuth.getInstance();
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
    });
    }


    // Update della visita con i dati del file
    private void UpdateToFirestore() {

        fStore = FirebaseFirestore.getInstance();

        fAuth = FirebaseAuth.getInstance();

        user_id= fAuth.getCurrentUser().getUid();

        DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Visita").document(visitaID);
        doc.update("file", filename+nomeVisita+ext);
        doc.update("message", message);

    }

    // Permette la condivisione del file via Bluetooth, Gmail ecc.
    private void shareMessage() {
        salvaFile();

        String stringFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" + filename+nomeVisita+ext;

        File file = new File(stringFile);
        contentUri = FileProvider.getUriForFile(RecapVisitaActivity.this, "com.example.android.fileprovider", file);

        //Controlla se il file esiste
        if(!file.exists()){
            Toast.makeText(RecapVisitaActivity.this, getResources().getString(R.string.file_non_esiste), Toast.LENGTH_LONG).show();
            return;
        }

        //Attributi che l'intent deve avere per poter effettuare lo share e il richiamo di applicazioni che consentono di ricevere file
        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("text/*");
        intentShare.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.la_mia_visita_a) + nomeLuogo); //per condividere con email app
        intentShare.putExtra(Intent.EXTRA_STREAM, contentUri);
        intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        RecapVisitaActivity.this.startActivity(Intent.createChooser(intentShare, String.valueOf(R.string.condividi_file)));


    }
    // Permette di verificare se e'possibile scrivere nella memoria esterna del telefono
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    //permette di salvare il file della visita sul telefono
    private void salvaFile() {
        if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            // Salva in: /storage/emulated/0/Downloads.
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    filename+nomeVisita+ext);

            try {
                //scrittura del file
                FileOutputStream f = new FileOutputStream(file);
                f.write(message.getBytes(StandardCharsets.UTF_8));
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, getResources().getString(R.string.file_salvato),Toast.LENGTH_LONG).show();
        } else {
            //se non si hanno i permessi per la scrittura sullo storage del telefono
            requestPermissions();
        }

    }

    // Permette di richiedere il permesso per la scrittura sullo storage del telefono
    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //mostro spiegazione del permesso richeisto
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.permnessi_salva_file))
                    .setMessage(getResources().getString(R.string.accetta))
                    .setPositiveButton(getResources().getString(R.string.accetta_a), (dialogInterface, i) -> ActivityCompat.requestPermissions(
                            RecapVisitaActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton(getResources().getString(R.string.annulla_account),
                            (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
    public boolean checkPermission(String permessi) {
        int check = ContextCompat.checkSelfPermission(this,permessi);
        return (check == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.consentire_autorizzazione));
                builder.setPositiveButton(getResources().getString(R.string.accetta), (dialog, which) -> { });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    // caricamento del messaggio da condividire o salvare sul telefono
    private void loadMessage() {

        //File path = getApplicationContext().getFilesDir();
        zonelist.remove("Seleziona Zona");
        message  =  getResources().getString(R.string.percorso_in_luogo) +nomeLuogo+ "\n " + getResources().getString(R.string.nomev) + nomeVisita + "\n " + getResources().getString(R.string.zone_visitate) + zonelist + "\n "+ getResources().getString(R.string.percorsi) + "\n ";
        for(int i = 0; i < zonelist.size()-1; i ++){
            for(int j = i+1; j<zonelist.size();j++) {
                if (!(graph.getAllEdges(zonelist.get(i), zonelist.get(j)).toString().equals("[]"))) {
                    message = message + "\n" + graph.getAllEdges(zonelist.get(i), zonelist.get(j)).toString();
                }
            }
        }



        Log.d(TAG," "+ message);
    }

    private class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_zona_inizio;
        private TextView list_zona_fine;
        private ImageView list_click;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_zona_inizio=itemView.findViewById(R.id.zonaInizio);
            list_zona_fine=itemView.findViewById(R.id.zonaFine);
            list_click = itemView.findViewById(R.id.image_click);
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
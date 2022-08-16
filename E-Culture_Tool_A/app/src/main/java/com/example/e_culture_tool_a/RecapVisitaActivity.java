package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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

import Models.Edge;

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
    String filename ="Visita.txt";
    Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

    List<String> zonelist = new ArrayList<>();
    Button mshare, msave;
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



        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreList=(RecyclerView) findViewById(R.id.item_rec_list);


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


    msave.setOnClickListener(view -> {

        loadMessage();
        salvaFile();

    });
        mshare.setOnClickListener(view -> {


            loadMessage();
            shareMessage();

          /*  File path = getApplicationContext().getFilesDir();
            File readFrom = new File(path,filename);
            Intent intentshare = new Intent(Intent.ACTION_SEND);
            intentshare.setType("txt");
            intentshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(readFrom.getPath()));
            Intent chooser = Intent.createChooser(intentshare,getString(R.string.scelta_app_condivisione_percorso));

            // Verifico che un'app sia disponibile
            if (chooser.resolveActivity(getPackageManager()) == null) {
                Toast.makeText(RecapVisitaActivity.this, R.string.app_per_condividere_non_trovata, Toast.LENGTH_SHORT).show();
                return;
            }

            // Avvio l'app scelta
            startActivity(chooser);
*/

        });

    }

    private void shareMessage() {
        salvaFile();

        String stringFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/" + filename;

        File file = new File(stringFile);
        contentUri = FileProvider.getUriForFile(RecapVisitaActivity.this, "com.example.android.fileprovider", file);

        //Controlla se il file esiste
        if(!file.exists()){
            Toast.makeText(RecapVisitaActivity.this, "file non esite", Toast.LENGTH_LONG).show();
            return;
        }

        //Attributi che l'intent deve avere per poter effettuare lo share e il richiamo di applicazioni che consentono di ricevere file
        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("text/*");
        intentShare.putExtra(Intent.EXTRA_SUBJECT, "Subject Here"); //per condividere con email app
        intentShare.putExtra(Intent.EXTRA_STREAM, contentUri);
        intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        RecapVisitaActivity.this.startActivity(Intent.createChooser(intentShare, "Condividi file"));


    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    private void salvaFile() {
        if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            // Salva in: /storage/emulated/0/Downloads.
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    filename); // tolgo .json dal nome

            try {
                FileOutputStream f = new FileOutputStream(file);
                f.write(message.getBytes(StandardCharsets.UTF_8));
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this,"file salvato" ,Toast.LENGTH_LONG).show();
        } else {
            requestPermissions();
        }

    }
    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //mostro spiegazione del permesso richeisto
            new AlertDialog.Builder(this)
                    .setTitle("Per salvare il file bisgna accettare i permessi")
                    .setMessage("Accetta")
                    .setPositiveButton("Accetta", (dialogInterface, i) -> ActivityCompat.requestPermissions(
                            RecapVisitaActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton("Annulla",
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
                //Toast.makeText(this, "Permesso concesso", Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("string");
                builder.setPositiveButton("ivnrivm", (dialog, which) -> { });
                AlertDialog dialog = builder.create();
                dialog.show();
                //Toast.makeText(this, "Permesso rifiutato", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadMessage() {

        //File path = getApplicationContext().getFilesDir();
        zonelist.remove("Seleziona Zona");
        message  =  " Percorso in luogo:" +nomeLuogo+ "\n Nome visita: " + nomeVisita + "\n zone visitate" + zonelist + "\n Percorsi:\n ";
        for(int i = 0; i < zonelist.size()-1; i ++){
            for(int j = i+1; j<zonelist.size();j++) {
                if (!(graph.getAllEdges(zonelist.get(i), zonelist.get(j)).toString().equals("[]"))) {
                    message = message + "\n" + graph.getAllEdges(zonelist.get(i), zonelist.get(j)).toString();
                }
            }
        }

        /*try {
            FileOutputStream writer = new FileOutputStream(new File(path,filename));
            writer.write(message.getBytes());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        */

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
            list_click = itemView.findViewById(R.id.image_multipla_click);

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






        /* int i;
        vs = (VerticalStepView) findViewById(R.id.verticalStep);

        DefaultDirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        zonelist = (ArrayList<String>) args.getSerializable("ARRAYLIST");
        Log.d(TAG,"zone:"+zonelist);

        for(i = 0 ; i < zonelist.size() ; i ++){

            graph.addVertex(zonelist.get(i));
        }
        for(i = 0 ; i < zonelist.size()-1 ; i ++){

                graph.addEdge(zonelist.get(i), zonelist.get(i + 1));


        }

        String id = "idtest";
        String nome = "nomepercorsotest";

        JSONObject visita = new JSONObject();
        try {
            visita.put("id", id);
            visita.put("name", nome);
            for(i = 0 ; i < zonelist.size() ; i ++){
                visita.put("zona"+i, zonelist.get(i));
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String jsonStr = visita.toString();
        String filename ="ciao.txt";

        FileOutputStream fo = null;
        try {
            fo = openFileOutput(filename,MODE_PRIVATE);
            fo.write(jsonStr.getBytes());
        } catch (FileNotFoundException e) {


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        vs.setStepsViewIndicatorComplectingPosition(zonelist.size()-1)
                .reverseDraw(false)
                .setStepViewTexts(zonelist)
                .setLinePaddingProportion(0.85f)
                .setStepsViewIndicatorCompletedLineColor(Color.parseColor("#FFFF00"))
                .setStepViewComplectedTextColor(Color.parseColor("#FFFF00"))
                .setStepViewComplectedTextColor(ContextCompat.getColor(this, com.baoyachi.stepview.R.color.uncompleted_text_color))
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(this, com.baoyachi.stepview.R.color.uncompleted_text_color))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(this, com.baoyachi.stepview.R.color.uncompleted_text_color))
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(this, com.baoyachi.stepview.R.drawable.default_icon))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(this, com.baoyachi.stepview.R.drawable.default_icon))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(this, com.baoyachi.stepview.R.drawable.default_icon));


*/


}
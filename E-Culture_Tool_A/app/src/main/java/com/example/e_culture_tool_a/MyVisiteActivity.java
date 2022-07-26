package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_culture_tool_a.Models.Visita;
import com.google.gson.Gson;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;

public class MyVisiteActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String user_id;
    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;
    Query query;
    Boolean flag;
    FloatingActionButton AddLuogo;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_visite);

        textView = findViewById(R.id.textView);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        AddLuogo =findViewById(R.id.AddLuogo);

        // // Controllo se l'utente accede come ospite e nascondo la funzione di aggiunta visita
        if(fAuth.getCurrentUser()==null) {
            textView.setText(R.string.visite);
            AddLuogo.setVisibility(View.INVISIBLE);
        }


        mFirestoreList = findViewById(R.id.list_visite);
        flag = false;
        Bundle extras = getIntent().getExtras();
        if(extras!=null) flag = extras.getBoolean("flag");


        if(flag){
            if(fAuth.getCurrentUser()==null){
                textView.setText(R.string.visite);
                query = fStore.collectionGroup("Visita");
            }else {
                // Se si enta con Button VisiteCuratori
                textView.setText(R.string.visite);
                user_id = fAuth.getCurrentUser().getUid();
                query = fStore.collectionGroup("Visita").whereNotEqualTo("author", user_id);
            }
        }else{
            // Se si entra con Button Myvisite
            user_id = fAuth.getCurrentUser().getUid();
            query = fStore.collection("utenti").document(user_id).collection("Visita");
        }
        FirestoreRecyclerOptions<Visita> options = new FirestoreRecyclerOptions.Builder<Visita>().setQuery(query, Visita.class).build();

        adapter = new FirestoreRecyclerAdapter<Visita, ProductsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Visita model) {
                holder.list_name.setText(model.getNome());
                if(!flag){
                    holder.list_options.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu popupMenu=new PopupMenu(MyVisiteActivity.this,view);
                            popupMenu.getMenuInflater().inflate(R.menu.visite_menu,popupMenu.getMenu());
                            popupMenu.show();
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    switch (menuItem.getItemId()){
                                        case R.id.IdAggiornaVisita:
                                            String id = model.getId();
                                            String nome = model.getNome();
                                            String author = model.getAuthor();
                                            String luogoID =model.getLuogoID();

                                            Intent i = new Intent(MyVisiteActivity.this, UpdateVisitaActivity.class);
                                            i.putExtra("id", id);
                                            i.putExtra("nome", nome);
                                            i.putExtra("author", author);
                                            i.putExtra("luogoID", luogoID);
                                            startActivity(i);
                                            break;
                                        case R.id.IdDeleteVisita:
                                            eliminaVisita(model);
                                            break;
                                    }

                                    return true;
                                }
                            });

                        }
                    });

                }else{
                    holder.list_options.setVisibility(View.INVISIBLE);
                }
                holder.list_onclick.setOnClickListener(view -> {
                    Intent intent = new Intent(MyVisiteActivity.this, ShowVisitaActivity.class);
                    Gson gson = new Gson();
                    String myJson = gson.toJson(model);
                    intent.putExtra("myjson", myJson);
                    startActivity(intent);




                });

            }

            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.visita_single_item, parent, false);
                return new ProductsViewHolder(view);
            }

            /*
            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Zone model) {
                holder.list_name.setText(model.getNome());

                holder.list_click.setOnClickListener(view -> {
                    String id = model.getId();
                    String nome = model.getNome();
                    String descrizione = model.getDescrizione();
                    String luogoID = model.getLuogoID();
                    Intent i = new Intent(MyVisiteActivity.this, UpdateVisitaActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("nome", nome);
                    i.putExtra("descrizione", descrizione);
                    i.putExtra("luogoID", luogoID);
                    startActivity(i);

                });
            }*/
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);


    }

    private void eliminaVisita(Visita model) {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyVisiteActivity.this);
        dialog.setTitle(R.string.elimina_visita);
        dialog.setMessage(R.string.sicuro_elimina_visita);
        dialog.setPositiveButton(getResources().getString(R.string.elimina_visita), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Visita").document(model.getId());
                doc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MyVisiteActivity.this, getResources().getString(R.string.visita_eliminata_ok), Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.annulla_account), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private class ProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView list_name;
        private ImageView list_options;
        private ImageView list_onclick;


        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name = itemView.findViewById(R.id.list_my_visite);
            list_options=itemView.findViewById(R.id.VisiteOptions);
            list_onclick = itemView.findViewById(R.id.image_click);

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




    public void addPercorso(View view) {
        startActivity(new Intent(getApplicationContext(), NewVisitaActivity.class));
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
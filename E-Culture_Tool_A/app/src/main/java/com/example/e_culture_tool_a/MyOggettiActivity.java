package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_culture_tool_a.Models.Oggetti;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;


public class MyOggettiActivity extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;

    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;

    private ImageView Tutorial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_oggetti);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreList=(RecyclerView) findViewById(R.id.firestore_oggetti_list);
        Tutorial=findViewById(R.id.Question_my_oggetto);

        Tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToutorial();
            }
        });


        // Query per ogni Oggetto del Utente Attuale
        Query query=fStore.collectionGroup("Oggetti").whereEqualTo("author", user_id);
        FirestoreRecyclerOptions<Oggetti> options=new FirestoreRecyclerOptions.Builder<Oggetti>().setQuery(query,Oggetti.class).build();

        adapter= new FirestoreRecyclerAdapter<Oggetti, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.oggetti_single_item,parent,false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Oggetti model) {
                holder.list_name.setText(model.getNome());

                if(model.getPhoto()!= null) {
                    Picasso.get().load(model.getPhoto()).into(holder.list_image);
                }

                holder.list_click.setOnClickListener(view -> {
                    String nomeOggetto = model.getNome();
                    String fotoOggetto = model.getPhoto();
                    String descrOggetto = model.getDescrizione();

                    Intent intent = new Intent(MyOggettiActivity.this, ShowOggetti.class);
                    intent.putExtra("idOggetto", model.getId());
                    intent.putExtra("fotoOggetto", fotoOggetto);
                    intent.putExtra("nomeOggetto", nomeOggetto);
                    intent.putExtra("descrOggetto", descrOggetto);

                    startActivity(intent);
                });


                holder.list_options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu=new PopupMenu(MyOggettiActivity.this,view);
                        popupMenu.getMenuInflater().inflate(R.menu.object_menu,popupMenu.getMenu());
                        popupMenu.show();
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.Id_multiple:
                                        String id = model.getId();
                                        String nome = model.getNome();
                                        String luogoID = model.getLuogoID();
                                        String zonaID = model.getZonaID();
                                        String photo = model.getPhoto();
                                        Intent i = new Intent(MyOggettiActivity.this, MyDomandeMultipleActivity.class);
                                        i.putExtra("id",id);
                                        i.putExtra("nome",nome);
                                        i.putExtra("photo",photo);
                                        i.putExtra("luogoID", luogoID);
                                        i.putExtra("zonaID", zonaID);
                                        startActivity(i);
                                    break;

                                    case R.id.Id_Tempo:
                                        String id2 = model.getId();
                                        String nome2 = model.getNome();
                                        String luogoID2 = model.getLuogoID();
                                        String zonaID2 = model.getZonaID();
                                        String photo2 = model.getPhoto();
                                        Intent i2 = new Intent(MyOggettiActivity.this, MyTempoDomandeActivity.class);
                                        i2.putExtra("id",id2);
                                        i2.putExtra("nome",nome2);
                                        i2.putExtra("photo",photo2);
                                        i2.putExtra("luogoID", luogoID2);
                                        i2.putExtra("zonaID", zonaID2);
                                        startActivity(i2);
                                    break;

                                    case R.id.Id_Modifica:
                                        String id3 = model.getId();
                                        String nome3 = model.getNome();
                                        String descrizione3 = model.getDescrizione();
                                        String author3 = model.getAuthor();
                                        String luogoID3 = model.getLuogoID();
                                        String zonaID3 = model.getZonaID();
                                        String photo3 = model.getPhoto();
                                        Intent i3 = new Intent(MyOggettiActivity.this, UpdateOggettiActivity.class);
                                        i3.putExtra("id",id3);
                                        i3.putExtra("nome",nome3);
                                        i3.putExtra("descrizione",descrizione3);
                                        i3.putExtra("photo",photo3);
                                        i3.putExtra("author", author3);
                                        i3.putExtra("luogoID", luogoID3);
                                        i3.putExtra("zonaID", zonaID3);
                                        startActivity(i3);
                                    break;

                                }
                                return true;
                            }
                        });
                    }
                });

                if(model.getPhoto()!= null) {
                    Picasso.get().load(model.getPhoto()).into(holder.list_image);
                }
            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

    }
    private class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_name;

        private ImageView list_image;

        private ImageView list_options;
        private MenuItem list_addTempo;
        private ImageView list_click;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name=itemView.findViewById(R.id.list_nome_oggetto);
            list_image=itemView.findViewById(R.id.imageOggetti);
            list_options = itemView.findViewById(R.id.options);
            list_click=itemView.findViewById(R.id.imageViewOggetti);


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


    public void addOggetto(View view) {

        startActivity( new Intent(getApplicationContext(), NewOggettoActivity.class));

    }
    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), UpdateProfile.class));


    }
    public void showToutorial(){showToutorialOggetto();
    }

    public void showToutorialOggetto(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(MyOggettiActivity.this)
                .setTarget(R.id.firestore_oggetti_list)
                .setPrimaryText(R.string.Titolo_My_Oggetti)
                .setSecondaryText(R.string.Descrizione_My_Oggetti)
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
                            showToutorial_Add_Oggetto();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorial_Add_Oggetto(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(MyOggettiActivity.this)
                .setTarget(R.id.AddOggetto)
                .setPrimaryText(R.string.Titolo_Add_Oggetti)
                .setSecondaryText(R.string.Descrizione_Add_Oggetti)
                .setPrimaryTextColour(color1)
                .setSecondaryTextColour(color1)
                .setBackgroundColour(color2)
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
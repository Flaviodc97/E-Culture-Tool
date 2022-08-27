package com.example.e_culture_tool_a;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import com.example.e_culture_tool_a.Models.Luogo;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class MyLuoghiActivity extends AppCompatActivity {
    public static final String TAG = "Prova";
    private TextView mluoghi;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String user_id;
    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;
    private ImageView Tutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miei_luoghi);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        Tutorial=findViewById(R.id.Question_my_luoghi);

        mFirestoreList=findViewById(R.id.firestore_luoghi_list);


        // Query per ottenere tutti i luoghi di un determinato utente
        Query query=fStore.collection("utenti").document(user_id).collection("Luoghi");
        FirestoreRecyclerOptions<Luogo> options=new FirestoreRecyclerOptions.Builder<Luogo>().setQuery(query,Luogo.class).build();

        adapter= new FirestoreRecyclerAdapter<Luogo, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.luoghi_single_item,parent,false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Luogo model) {

                holder.list_name.setText(model.getNome());
                if(model.getPhoto()!= null) {
                    Picasso.get().load(model.getPhoto()).into(holder.list_image);
                }

                holder.list_click.setOnClickListener(view -> {
                    String nomeLuogo = model.getNome();
                    String fotoLuogo = model.getPhoto();
                    String descrLuogo = model.getDescrizione();
                    Intent intent = new Intent(MyLuoghiActivity.this, ShowLuoghi.class);
                    intent.putExtra("fotoLuogo", fotoLuogo);
                    intent.putExtra("nomeLuogo", nomeLuogo);
                    intent.putExtra("descrLuogo", descrLuogo);
                    startActivity(intent);
                });

                holder.list_option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu=new PopupMenu(MyLuoghiActivity.this,view);
                        popupMenu.getMenuInflater().inflate(R.menu.luoghi_menu,popupMenu.getMenu());
                        popupMenu.show();
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.IdAggiornaLuogo:
                                        String id= model.getId();
                                        String nome= model.getNome();
                                        String descrizione = model.getDescrizione();
                                        String photo = model.getPhoto();
                                        Intent i = new Intent(MyLuoghiActivity.this, UpdateLuogoActivity.class);
                                        i.putExtra("id",id);
                                        i.putExtra("nome",nome);
                                        i.putExtra("descrizione",descrizione);
                                        i.putExtra("photo",photo);
                                        startActivity(i);

                                    break;
                                    case R.id.IdDeleteLuogo:
                                        deleteLuogo(model.getId());
                                    break;
                                }
                                return true;
                            }
                        });
                    }
                });


            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);


        Tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToutorial();
            }
        });

    }


    private class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_name;
        private ImageView list_image;
        private ImageView list_option;
        private ImageView list_click;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name=itemView.findViewById(R.id.list_luoghi);

            list_image=itemView.findViewById(R.id.Image_Luoghi);
            list_option= itemView.findViewById(R.id.LuoghiOptions);
            list_click=itemView.findViewById(R.id.imageViewLuoghi);
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

    public void addLuogo(View view) {

        startActivity( new Intent(getApplicationContext(), NewLuogoActivity.class));

    }

    public void deleteLuogo(String id) {
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        user_id = fauth.getCurrentUser().getUid();
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyLuoghiActivity.this);
        dialog.setTitle(R.string.elimina_luogo);
        dialog.setMessage(R.string.sicuro_elimina_luogo);
        dialog.setPositiveButton(getResources().getString(R.string.elimina_luogo), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DocumentReference doc = fStore.collection("utenti").document(user_id).collection("Luoghi").document(id);
                doc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MyLuoghiActivity.this, getResources().getString(R.string.luogo_eliminato_ok), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), HomeCuratoreActivity.class ));
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
    public void showToutorial(){
        showToutorialLuogo();
    }

    public void showToutorialLuogo(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(MyLuoghiActivity.this)
                .setTarget(R.id.firestore_luoghi_list)
                .setPrimaryText(R.string.Titolo_My_Luogo)
                .setSecondaryText(R.string.Descrizione_My_Luogo)
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
                           showToutorial_Add_Luogo();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorial_Add_Luogo(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(MyLuoghiActivity.this)
                .setTarget(R.id.AddLuogo)
                .setPrimaryText(R.string.Titolo_Add_Luogo)
                .setSecondaryText(R.string.Descrizione_Add_Luogo)
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
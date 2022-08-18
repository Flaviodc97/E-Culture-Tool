package com.example.e_culture_tool_a;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.e_culture_tool_a.Models.Zone;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

public class MyZoneActivity extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String user_id;

    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;

    ImageView Tutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_zone);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user_id = fAuth.getCurrentUser().getUid();
        mFirestoreList=findViewById(R.id.firestore_zone_list);

        Tutorial=findViewById(R.id.Question_my_zone);

        Tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showToutorial();
            }
        });




        Query query=fStore.collectionGroup("Zone").whereEqualTo("author", user_id);
        FirestoreRecyclerOptions<Zone> options=new FirestoreRecyclerOptions.Builder<Zone>().setQuery(query,Zone.class).build();

        adapter= new FirestoreRecyclerAdapter<Zone, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zone_single_item,parent,false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Zone model) {
                holder.list_name.setText(model.getNome());

                holder.list_options.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu=new PopupMenu(MyZoneActivity.this,view);
                        popupMenu.getMenuInflater().inflate(R.menu.zone_menu,popupMenu.getMenu());
                        popupMenu.show();
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.IdAggiornaZona:
                                        String id = model.getId();
                                        String nome = model.getNome();
                                        String descrizione = model.getDescrizione();
                                        String luogoID = model.getLuogoID();
                                        Intent i = new Intent(MyZoneActivity.this, UpdateZonaActivity.class);
                                        i.putExtra("id", id);
                                        i.putExtra("nome", nome);
                                        i.putExtra("descrizione", descrizione);
                                        i.putExtra("luogoID", luogoID);
                                        startActivity(i);
                                        break;
                                    case R.id.IdDeleteZona:
                                        Toast.makeText(MyZoneActivity.this, "CANCELLA", Toast.LENGTH_SHORT).show();
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


    }
    private class ProductsViewHolder extends RecyclerView.ViewHolder{
        private TextView list_name;
        ImageView list_options;



        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            list_name=itemView.findViewById(R.id.list_my_zone);
            list_options=itemView.findViewById(R.id.ZoneOptions);
        }
    }
    public void addZone(View view) {

        startActivity( new Intent(getApplicationContext(), NewZoneActivity.class));

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
    public void showToutorial(){showToutorialZone();
    }

    public void showToutorialZone(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(MyZoneActivity.this)
                .setTarget(R.id.firestore_zone_list)
                .setPrimaryText(R.string.Titolo_My_Zone)
                .setSecondaryText(R.string.Descrizione_My_Zone)
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
                            showToutorial_Add_Zone();
                        }
                    }
                })
                .show();
        //
    }

    public void showToutorial_Add_Zone(){
        //
        int color1 = ContextCompat.getColor(getApplicationContext(),R.color.white);
        int color2 = ContextCompat.getColor(getApplicationContext(),R.color.Primario);

        new MaterialTapTargetPrompt.Builder(MyZoneActivity.this)
                .setTarget(R.id.AddOggetto)
                .setPrimaryText(R.string.Titolo_Add_Zone)
                .setSecondaryText(R.string.Descrizione_Add_Zone)
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
package com.example.e_culture_tool_a;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_culture_tool_a.Fragment.LuoghiFragment;
import com.squareup.picasso.Picasso;

public class ShowLuoghi extends AppCompatActivity {

    ImageView fotoLuogo;
    TextView nomeLuogo;
    TextView descrLuogo;

    String fL;
    String nL;
    String dL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_luoghi);

        fotoLuogo = findViewById(R.id.fotoLuogo);
        nomeLuogo = findViewById(R.id.nomeLuogo);
        descrLuogo = findViewById(R.id.descrLuogo);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fL = extras.getString("fotoLuogo");
            nL = extras.getString("nomeLuogo");
            dL = extras.getString("descrLuogo");

        }

        if(fL!=null){
            Picasso.get().load(fL).into(fotoLuogo);
        }

        nomeLuogo.setText(nL);
        descrLuogo.setText(dL);
    }
}
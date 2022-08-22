package com.example.e_culture_tool_a;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ShowZone extends AppCompatActivity {

    TextView nomeZona;
    TextView descrZona;

    String nZ;
    String dZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_zone);

        nomeZona = findViewById(R.id.nomeZona);
        descrZona = findViewById(R.id.descrZona);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nZ = extras.getString("nomeZona");
            dZ = extras.getString("descrZona");

        }

        nomeZona.setText(nZ);
        descrZona.setText(dZ);
    }
}
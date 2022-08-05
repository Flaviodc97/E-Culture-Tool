package com.example.e_culture_tool_a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class HomeVisitatoreActivity extends AppCompatActivity {
    ImageButton myvisiteButton;
    ImageView visiteCuratoriButton;
    ImageView qrButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_visitatore);
        qrButton = (ImageView)findViewById(R.id.QrImg);
        myvisiteButton = (ImageButton) findViewById(R.id.VisiteVisitatori);
        visiteCuratoriButton = (ImageView)findViewById(R.id.Visiteimg);
        visiteCuratoriButton.setOnClickListener(view -> {

            startActivity( new Intent(getApplicationContext(), MyVisiteActivity.class));

        });
        myvisiteButton.setOnClickListener(view -> {
            startActivity( new Intent(getApplicationContext(), MyVisiteActivity.class));
        });
        qrButton.setOnClickListener( view -> {

            Toast.makeText(HomeVisitatoreActivity.this, "QRActivity Work in progress",Toast.LENGTH_LONG).show();
        });
    }

    public void goHome(View view) {
        startActivity( new Intent(getApplicationContext(), HomeVisitatoreActivity.class));


    }
    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profile.class));

    }
}
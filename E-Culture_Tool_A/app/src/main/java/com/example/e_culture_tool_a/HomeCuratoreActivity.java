package com.example.e_culture_tool_a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class HomeCuratoreActivity extends AppCompatActivity {
    Button mbuttonProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mbuttonProfile = findViewById(R.id.ButtonProfile);

        mbuttonProfile.setOnClickListener(view -> {

            startActivity( new Intent(getApplicationContext(), Profile.class));
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity( new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bottombar, menu);
        return true;
    }


    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profile.class));


    }


    public void goHome(View view) {
        startActivity( new Intent(getApplicationContext(), HomeCuratoreActivity.class));
    }
}
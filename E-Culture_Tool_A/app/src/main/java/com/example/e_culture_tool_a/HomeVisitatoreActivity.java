package com.example.e_culture_tool_a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class HomeVisitatoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_visitatore);
    }

    public void goHome(View view) {
        startActivity( new Intent(getApplicationContext(), HomeVisitatoreActivity.class));


    }
    public void goProfile(MenuItem item) {

        startActivity( new Intent(getApplicationContext(), Profile.class));


    }
}
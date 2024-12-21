package com.example.courtmaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

    }

    public void FindCourts(View view) {
        Intent FindCourts = new Intent(MainScreen.this, Sign_Places.class);
        startActivity(FindCourts);
    }

    public void train(View view) {
        Intent Built_In = new Intent(MainScreen.this, Built_In_Programs.class);
        startActivity(Built_In);
    }

    public void Personal_Program(View view) {
        Intent PersonalTraining = new Intent(MainScreen.this, Personal_Program.class);
        startActivity(PersonalTraining);
    }
}
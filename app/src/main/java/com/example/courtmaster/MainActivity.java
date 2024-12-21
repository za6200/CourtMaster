package com.example.courtmaster;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Intent Start, Registration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Start = new Intent(this, Sign_Places.class);
        Registration = new Intent(this, Registration.class);
        startActivity(Registration);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        startActivity(Registration);
    }
}
package com.example.courtmaster;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Show_Program extends AppCompatActivity {

    Intent Training_Program;
    TextView ProgramTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_program);
        ProgramTV = findViewById(R.id.ProgramTV);
        Training_Program = getIntent();
        Training_Program trainingProgram = (Training_Program) Training_Program.getSerializableExtra("Training_Program");
        ProgramTV.setText(trainingProgram.getName());

    }
}
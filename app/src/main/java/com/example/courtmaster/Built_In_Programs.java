package com.example.courtmaster;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Built_In_Programs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_built_in_programs);
        addProgramToDB();

    }

    public void addProgramToDB()
    {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise("Normal free-throw", 50, "Easy","https://www.youtube.com/watch?v=HaObGL_9Q2I", "Practice shooting from the free-throw line."));
        exercises.add(new Exercise("Clean free-throw", 4,"Easy", "https://www.youtube.com/watch?v=-qySIh0H1Ug", "Practice shooting clean free-throw in a row."));
        exercises.add(new Exercise("Clean free-throw (2)", 8,"Medium", "https://www.youtube.com/watch?v=-qySIh0H1Ug", "Practice shooting clean free-throw in a row."));
        exercises.add(new Exercise("*Clean free-throw (3)", 12,"Hard", "https://www.youtube.com/watch?v=-qySIh0H1Ug", "Practice shooting clean free-throw in a row."));

        Training_Program trainingProgram = new Training_Program("Free-Throw Practice", "Practice shooting from the free-throw line, focusing on form, aim, and consistency.",exercises );

        saveTrainingProgram(trainingProgram);
    }

    private void saveTrainingProgram(Training_Program program) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Set the user and save the training program
            FBRef.getUser(currentUser);
            FBRef.saveTrainingProgram(program, Built_In_Programs.this);
        } else {
            Toast.makeText(Built_In_Programs.this, "Please log in first.", Toast.LENGTH_SHORT).show();
        }
    }
}
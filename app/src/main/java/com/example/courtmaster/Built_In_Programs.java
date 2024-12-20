package com.example.courtmaster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class Built_In_Programs extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView ProgramList;
    List<Training_Program> trainingProgramList;
    Intent ShowProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_built_in_programs);
        ProgramList = findViewById(R.id.listViewPrograms);
        ShowProgram = new Intent(Built_In_Programs.this, Show_Program.class);

        trainingProgramList = addProgramToDB();
        addProgramsToList(trainingProgramList);
    }

    public void addProgramsToList(List<Training_Program> trainingPrograms) {
        List<String> ProgramsNames = new ArrayList<>();
        for (Training_Program i : trainingPrograms) {
            ProgramsNames.add(i.getName());
        }

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ProgramsNames);
        ProgramList.setAdapter(adp);

        ProgramList.setOnItemClickListener(this);
    }

    public List<Training_Program> addProgramToDB() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise("Normal free-throw", 50, "Easy", "https://www.youtube.com/watch?v=HaObGL_9Q2I", "Practice shooting from the free-throw line."));
        exercises.add(new Exercise("Clean free-throw", 4, "Easy", "https://www.youtube.com/watch?v=-qySIh0H1Ug", "Practice shooting clean free-throws in a row."));
        exercises.add(new Exercise("Clean free-throw (2)", 8, "Medium", "https://www.youtube.com/watch?v=-qySIh0H1Ug", "Practice shooting clean free-throws in a row."));
        exercises.add(new Exercise("*Clean free-throw (3)", 12, "Hard", "https://www.youtube.com/watch?v=-qySIh0H1Ug", "Practice shooting clean free-throws in a row."));
        Training_Program trainingProgram = new Training_Program("Free-Throw Practice", "Practice shooting from the free-throw line, focusing on form, aim, and consistency.", exercises);
        saveTrainingProgram(trainingProgram);

        List<Exercise> exercises2 = new ArrayList<>();
        exercises2.add(new Exercise("3 pointer", 20, "Easy", "https://www.youtube.com/watch?v=8A2-IczVWS8", "Shoot from the corner, wing, center, second wing, second corner: 4 shots each"));
        exercises2.add(new Exercise("Clean 3 pointer", 3, "Easy", "https://www.youtube.com/watch?v=8A2-IczVWS8", "Practice shooting clean 3 pointer from the wing center and second wing (1 each spot) in a row."));
        exercises2.add(new Exercise("Clean 3 pointer (2)", 6, "Medium", "https://www.youtube.com/watch?v=8A2-IczVWS8", "Practice shooting clean 3 pointer from the wing center and second wing (2 each spot) in a row."));
        exercises2.add(new Exercise("*Clean 3 pointer (3)", 9, "Hard", "https://www.youtube.com/watch?v=8A2-IczVWS8", "Practice shooting clean 3 pointer from the wing center and second wing (3 each spot) in a row."));
        Training_Program trainingProgram2 = new Training_Program("3 Pointer Practice", "Practice shooting 3 pointers with focus on form, aim, and consistency.", exercises2);
        saveTrainingProgram(trainingProgram2);

        List<Training_Program> trainingPrograms = new ArrayList<>();
        trainingPrograms.add(trainingProgram);
        trainingPrograms.add(trainingProgram2);

        return trainingPrograms;
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Training_Program clickedProgram = trainingProgramList.get(position);
        ShowProgram.putExtra("Training Program", clickedProgram);
        startActivity(ShowProgram);

    }
}

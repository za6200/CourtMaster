package com.example.courtmaster;

import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Personal_Program extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner LevelSpin;
    String[] Levels = {"Easy", "Medium", "Hard", "Insane"};
    Exercise exercise;
    Training_Program PersonalProgram;
    TextView PersonalProgramTV;
    EditText ProgramNameET, ProgramDescriptionET, ExNameET, ExRepeatET, ExVideoIdET, ExDescriptionET;
    Button NextExerciseBtn, Finish;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_program);
        LevelSpin = findViewById(R.id.LevelSpin);
        ProgramNameET = findViewById(R.id.ProgramNameET);
        ProgramDescriptionET = findViewById(R.id.ProgramDescriptionET);
        ExNameET = findViewById(R.id.ExNameET);
        ExRepeatET = findViewById(R.id.ExRepeatET);
        ExVideoIdET = findViewById(R.id.ExVideoIdET);
        ExDescriptionET = findViewById(R.id.ExDescriptionET);
        NextExerciseBtn = findViewById(R.id.NextExerciseBtn);
        Finish = findViewById(R.id.Finish);
        PersonalProgramTV = findViewById(R.id.PersonalProgramTV);

        ArrayAdapter<String> LevelAdp = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Levels);
        LevelSpin.setAdapter(LevelAdp);
        LevelSpin.setOnItemSelectedListener(this);

        PersonalProgram = new Training_Program();

        if (PersonalProgram.getProgram() == null) {
            PersonalProgram.setProgram(new ArrayList<>());
        }
    }

    public void Next_Exercise(View view) {

        // Input validation
        String exerciseName = ExNameET.getText().toString();
        String repeatText = ExRepeatET.getText().toString();
        String videoId = ExVideoIdET.getText().toString();
        String description = ExDescriptionET.getText().toString();



        if (exerciseName.isEmpty() || repeatText.isEmpty() || videoId.isEmpty() || description.isEmpty()) {
            Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        int repeat = parseInt(repeatText);

        if (repeat <= 0 || repeat > 100) {
            Toast.makeText(getApplicationContext(), "Repeat must be between 1 and 100", Toast.LENGTH_SHORT).show();
            return;
        }

        if (counter > 5) {
            Toast.makeText(getApplicationContext(), "Can't add more than 5 exercises", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set exercise values
        /*exercise.setName(exerciseName);
        exercise.setRepeat(repeat);
        exercise.setUrl(videoId);
        exercise.setDescription(description);
        exercise.setLevel(Levels[LevelSpin.getSelectedItemPosition()]);*/

        exercise = new Exercise(exerciseName, repeat, Levels[LevelSpin.getSelectedItemPosition()], videoId, description);

        // Set the PersonalProgram name and description only once
        if (counter == 0) {
            PersonalProgram.setName(ProgramNameET.getText().toString());
            PersonalProgram.setDescription(ProgramDescriptionET.getText().toString());
            PersonalProgramTV.setText(ProgramNameET.getText().toString());
            ProgramNameET.setVisibility(View.INVISIBLE);
            ProgramDescriptionET.setVisibility(View.INVISIBLE);
        }

        PersonalProgram.getProgram().add(exercise);
        ExNameET.setText("");
        ExRepeatET.setText("");
        ExVideoIdET.setText("");
        ExDescriptionET.setText("");
        counter++;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void Finish(View view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TrainingPrograms");
        databaseReference.child(PersonalProgram.getName()).setValue(PersonalProgram).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Exercise added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error adding exercise", Toast.LENGTH_SHORT).show();
            }
        });
        Intent MainScreen = new Intent(Personal_Program.this, MainScreen.class);
        startActivity(MainScreen);
    }
}

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Personal_Program extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner LevelSpin;
    String[] Levels = {"Easy", "Medium", "Hard", "Insane"};
    Exercise exercise;
    Training_Program PersonalProgram;
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

        ArrayAdapter<String> LevelAdp = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Levels);
        LevelSpin.setAdapter(LevelAdp);
        LevelSpin.setOnItemSelectedListener(this);

        exercise = new Exercise();
        PersonalProgram = new Training_Program();
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
        exercise.setName(exerciseName);
        exercise.setRepeat(repeat);
        exercise.setLevel("medium");
        exercise.setUrl(videoId);
        exercise.setDescription(description);
        exercise.setLevel(Levels[LevelSpin.getSelectedItemPosition()]);

        // Set the PersonalProgram name and description only once
        if (counter == 0) {
            PersonalProgram.setName(ProgramNameET.getText().toString());
            PersonalProgram.setDescription(ProgramDescriptionET.getText().toString());
            PersonalProgram.addExercise(exercise);
        }
        //Toast.makeText(getApplicationContext(), "repeat: " + exercise., Toast.LENGTH_SHORT).show();
        PersonalProgram.addExercise(exercise);

        // Push the updated program to the database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TrainingPrograms").child(PersonalProgram.getName());
        String programId = databaseReference.push().getKey(); // Use push() for a unique ID for each new program
        if (programId != null) {
            databaseReference.child(programId).setValue(PersonalProgram)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Exercise added successfully!", Toast.LENGTH_SHORT).show();
                            counter++;
                        } else {
                            Toast.makeText(getApplicationContext(), "Error adding exercise", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        ExNameET.setText("");
        ExRepeatET.setText("");
        ExVideoIdET.setText("");
        ExDescriptionET.setText("");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        exercise.setLevel(Levels[i]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void Finish(View view) {
        Intent MainScreen = new Intent(Personal_Program.this, MainScreen.class);
        startActivity(MainScreen);
    }
}

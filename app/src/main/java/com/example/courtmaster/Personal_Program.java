package com.example.courtmaster;

import static java.lang.Integer.parseInt;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Personal_Program extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner LevelSpin;
    String[] Levels= {"Easy", "Medium", "Hard", "Insane"};
    Exercise exercise;
    Training_Program PersonalProgram;
    EditText ProgramNameET, ProgramDescriptionET, ExNameET, ExRepeatET, ExVideoIdET, ExDescriptionET;
    Button NextExerciseBtn;
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
        ArrayAdapter<String> LevelAdp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Levels);
        LevelSpin.setAdapter(LevelAdp);
        LevelSpin.setOnItemSelectedListener(this);

    }

    public void Next_Exercise(View view) {
        exercise.setName(ExNameET.getText().toString());
        exercise.setRepeat(parseInt(ExRepeatET.getText().toString()));
        exercise.setUrl(ExVideoIdET.getText().toString());
        exercise.setDescription(ExDescriptionET.getText().toString());
        PersonalProgram.setName(ProgramNameET.getText().toString());
        PersonalProgram.setDescription(ProgramNameET.getText().toString());
        PersonalProgram.addExercise(exercise);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        exercise.setLevel(Levels[i]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
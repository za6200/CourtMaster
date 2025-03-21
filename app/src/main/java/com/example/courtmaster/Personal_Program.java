package com.example.courtmaster;

import static java.lang.Integer.parseInt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Personal_Program extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner LevelSpin;
    String[] Levels = {"Easy", "Medium", "Hard", "Insane"};
    Exercise exercise;
    Training_Program PersonalProgram;
    TextView PersonalProgramTV;
    EditText ProgramNameET, ProgramDescriptionET, ExNameET, ExRepeatET, ExVideoIdET, ExDescriptionET;
    Button NextExerciseBtn, Finish;
    String exerciseName, repeatText, videoId, description;
    int counter = 0, sureCheck = 0;

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

        PersonalProgram.setName("Program Name");
        PersonalProgram.setDescription("Program Description");
        ExNameET.setText("");
        ExRepeatET.setText("");
        ExVideoIdET.setText("");
        ExDescriptionET.setText("");

        if (PersonalProgram.getProgram() == null) {
            PersonalProgram.setProgram(new ArrayList<>());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void loadCurrentUser() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference("Users").child(currentUid);
        PersonalProgram.setCreator(currentUid);
    }

    public void Finish(View view) {
        if (ProgramNameET.getText().toString().equals("Program Name") || ProgramNameET.getText().toString().isEmpty() || ProgramDescriptionET.getText().toString().equals("Program Description") || ProgramDescriptionET.getText().toString().isEmpty()) {
            showAlertDialog("All Required fields must be filled");
            return;
        } else if (PersonalProgram.getProgram().isEmpty()) {
            showAlertDialog("No exercise added");
            return;
        }
        FBRef.refTrainingPrograms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean nameExists = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Training_Program existingProgram = snapshot.getValue(Training_Program.class);

                    if (existingProgram != null && existingProgram.getName().equalsIgnoreCase(ProgramNameET.getText().toString())) {
                        nameExists = true;
                        break;
                    }
                }

                if (nameExists) {
                    showAlertDialog("A program with this name already exists. Please choose a different name.");
                    ProgramNameET.setVisibility(View.VISIBLE);
                    PersonalProgramTV.setText(ProgramNameET.getText().toString());
                }

                else if(sureCheck < 1) {
                    PersonalProgramTV.setText(ProgramNameET.getText().toString());
                    PersonalProgram.setName(ProgramNameET.getText().toString());
                    showSureAlertDialog("Are you sure you want to submit Program: " + PersonalProgram.getName());
                    sureCheck++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void showSureAlertDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        loadCurrentUser();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TrainingPrograms");
                        databaseReference.child(PersonalProgram.getName())
                                .setValue(PersonalProgram)
                                .addOnCompleteListener(task -> {
                                    if (!task.isSuccessful()) {
                                        showAlertDialog("Error adding program");
                                    }
                                });

                        Intent MainScreen = new Intent(Personal_Program.this, MainScreen.class);
                        startActivity(MainScreen);

                        // Clear text fields
                        ExNameET.setText("");
                        ExRepeatET.setText("");
                        ExVideoIdET.setText("");
                        ExDescriptionET.setText("");
                    }
                })
                .setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sureCheck = 0;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void go_back(View view) {
        Intent MainScreen = new Intent(Personal_Program.this, MainScreen.class);
        startActivity(MainScreen);
    }

    public void nextEx(View view) {
        exerciseName = ExNameET.getText().toString();
        repeatText = ExRepeatET.getText().toString();
        videoId = ExVideoIdET.getText().toString();
        description = ExDescriptionET.getText().toString();



        if (exerciseName.isEmpty() || repeatText.isEmpty() || description.isEmpty()) {
            showAlertDialog("All Required fields must be filled");
            return;
        }

        int repeat = parseInt(repeatText);

        if (repeat <= 0 || repeat > 100) {
            showAlertDialog("Repeat must be between 1 and 100");
            return;
        }

        if (counter > 5) {
            showAlertDialog("Can't add more than 5 exercises");
            return;
        }

        exercise = new Exercise(exerciseName, repeat, Levels[LevelSpin.getSelectedItemPosition()], videoId, description);

        // Set the PersonalProgram name and description only once
        if (counter == 0) {
            if (ProgramNameET.getText().toString().equals("Program Name") || ProgramNameET.getText().toString().equals("") || ProgramDescriptionET.getText().toString().equals("Program Description") || ProgramDescriptionET.getText().toString().equals("")) {
                showAlertDialog("All Required fields must be filled");
                return;
            }
            PersonalProgram.setName(ProgramNameET.getText().toString());
            PersonalProgram.setDescription(ProgramDescriptionET.getText().toString());
            PersonalProgramTV.setText(ProgramNameET.getText().toString());
        }
        if(PersonalProgram.getName().equals("Program Name") || PersonalProgram.getDescription().equals("Program Description"))
        {
            showAlertDialog("Program Name and Description fields must be filled");
            return;
        }
        else {
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String st = item.getTitle() != null ? item.getTitle().toString().trim() : "";

        if (st.equalsIgnoreCase("Registration")) {
            startActivity(new Intent(this, Registration.class));
        } else if (st.equalsIgnoreCase("Main screen")) {
            startActivity(new Intent(this, MainScreen.class));
        } else if (st.equalsIgnoreCase("Built in")) {
            startActivity(new Intent(this, Built_In_Programs.class));
        } else if (st.equalsIgnoreCase("Personal")) {
            startActivity(new Intent(this, Personal_Program.class));
        } else if (st.equalsIgnoreCase("Find courts")) {
            startActivity(new Intent(this, Sign_Places.class));
        } else if (st.equalsIgnoreCase("My programs")) {
            startActivity(new Intent(this, My_Programs.class));
        } else {
            Toast.makeText(this, "Unknown option selected", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}

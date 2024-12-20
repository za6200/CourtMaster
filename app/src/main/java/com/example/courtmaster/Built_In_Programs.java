package com.example.courtmaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        trainingProgramList = new ArrayList<>();
        addProgramsToList();
    }

    public void addProgramsToList() {
        DatabaseReference programsRef = FirebaseDatabase.getInstance().getReference("TrainingPrograms");

        programsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trainingProgramList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Training_Program trainingProgram = snapshot.getValue(Training_Program.class);
                    if (trainingProgram != null) {
                        trainingProgramList.add(trainingProgram);
                    }
                }
                addProgramsToAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Built_In_Programs.this, "Error loading programs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addProgramsToAdapter() {
        List<String> programNames = new ArrayList<>();
        for (Training_Program program : trainingProgramList) {
            programNames.add(program.getName());
        }

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, programNames);
        ProgramList.setAdapter(adp);
        ProgramList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Training_Program clickedProgram = trainingProgramList.get(position);
        ShowProgram = new Intent(Built_In_Programs.this, Show_Program.class);
        ShowProgram.putExtra("Training Program", clickedProgram);
        startActivity(ShowProgram);
    }
}

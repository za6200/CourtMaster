package com.example.courtmaster;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Built_In_Programs extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView ProgramList;
    List<Training_Program> trainingProgramList;
    Intent ShowProgram;
    private ProgressDialog wating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_built_in_programs);
        ProgramList = findViewById(R.id.listViewPrograms);

        trainingProgramList = new ArrayList<>();
         wating = ProgressDialog.show(this, "Find Courts", "Searching...", true);
        addProgramsToList();
    }

    public void addProgramsToList() {
        DatabaseReference programsRef = FirebaseDatabase.getInstance().getReference("TrainingPrograms");

        programsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trainingProgramList.clear();

                // Populate the list with programs from the database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Training_Program trainingProgram = snapshot.getValue(Training_Program.class);
                    if (trainingProgram != null) {
                        trainingProgramList.add(trainingProgram);
                    }
                }

                // Sort the list by rating in descending order
                Collections.sort(trainingProgramList, new Comparator<Training_Program>() {
                    @Override
                    public int compare(Training_Program p1, Training_Program p2) {
                        return Float.compare(p2.getRating(), p1.getRating()); // Descending order
                    }
                });

                // Update the adapter with the sorted list
                addProgramsToAdapter();
                wating.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                System.err.println("Error fetching programs: " + databaseError.getMessage());
            }
        });
    }

    private void addProgramsToAdapter() {

        ProgramAdapter adp = new ProgramAdapter(this, trainingProgramList);
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

    public void go_back(View view) {
        ShowProgram = new Intent(Built_In_Programs.this, MainScreen.class);
        startActivity(ShowProgram);
    }
}

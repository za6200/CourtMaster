package com.example.courtmaster;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.List;

public class My_Programs extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView ProgramList;
    private List<Training_Program> trainingProgramList;
    private Intent ShowProgram;
    private ProgressDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_programs);

        ProgramList = findViewById(R.id.listViewPrograms);
        trainingProgramList = new ArrayList<>();

        // Show a progress dialog until data is loaded
        waitingDialog = ProgressDialog.show(this, "Loading Programs", "Please wait...", true
        );
        loadUserPrograms();
    }

    private void loadUserPrograms() {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference programsRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUid)
                .child("programs");

        programsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trainingProgramList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Training_Program trainingProgram = snapshot.getValue(Training_Program.class);
                    if (trainingProgram != null) {
                        trainingProgramList.add(trainingProgram);
                    }
                }

                updateListView();
                if (waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showAlertDialog("Error loading programs: " + databaseError.getMessage());
                if (waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
            }
        });
    }

    private void updateListView() {
        List<Float> programRating = new ArrayList<>();
        for (Training_Program program : trainingProgramList) {
            programRating.add(program.getRating());
        }

        ProgramAdapter adapter = new ProgramAdapter(this, trainingProgramList);
        ProgramList.setAdapter(adapter);
        ProgramList.setOnItemClickListener(this);
        waitingDialog.dismiss();
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, android.view.View view, int position, long id) {
        Training_Program clickedProgram = trainingProgramList.get(position);
        ShowProgram = new Intent(My_Programs.this, Show_Program.class);
        ShowProgram.putExtra("Training Program", clickedProgram);
        ShowProgram.putExtra("My program", true);
        startActivity(ShowProgram);
    }

    public void go_back(View view) {
        ShowProgram = new Intent(My_Programs.this, MainScreen.class);
        startActivity(ShowProgram);
    }
}

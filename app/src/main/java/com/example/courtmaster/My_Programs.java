package com.example.courtmaster;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

        DatabaseReference programsRef = FirebaseDatabase.getInstance().getReference("TrainingPrograms");

        programsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trainingProgramList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Training_Program trainingProgram = snapshot.getValue(Training_Program.class);
                    if (trainingProgram.getCreator().equals(currentUid)) {
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
        ProgramAdapter adp = new ProgramAdapter(this, trainingProgramList);
        ProgramList.setAdapter(adp);
        ProgramList.setOnItemClickListener(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
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

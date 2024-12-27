package com.example.courtmaster;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBRef {
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();

    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refUsers = FBDB.getReference("Users");
    public static DatabaseReference refTrainingPrograms = FBDB.getReference("TrainingPrograms");
    public static DatabaseReference refCourts = FBDB.getReference("Courts");
    public static DatabaseReference refTasks;
    public static DatabaseReference refDoneTasks;

    public static String uid;

    public static void getUser(FirebaseUser fbuser) {
        uid = fbuser.getUid();
        refTasks = FBDB.getReference("Tasks").child(uid);
        refDoneTasks = FBDB.getReference("Done_Tasks").child(uid);
    }

    public static void saveTrainingProgram(Training_Program program, Context context) {
        if (uid != null) {
            DatabaseReference userTrainingProgramRef = refTrainingPrograms.child(program.getName());
            userTrainingProgramRef.setValue(program)
                    .addOnCompleteListener(task -> {
                        if (!(task.isSuccessful())) {
                            Toast.makeText(context, "Error saving training program", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(context, "Sign in first", Toast.LENGTH_LONG).show();
        }
    }

}

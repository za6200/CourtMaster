package com.example.courtmaster;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class loadUser{
    private Context mContext;

    // Pass a context from the calling Activity
    public loadUser(Context context) {
        this.mContext = context;
    }

    public void loadCurrentUser(Training_Program PersonalProgram) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user;
                if (snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                } else {
                    user = new User();
                    user.setUid(currentUid);
                }

                if (user.getPrograms() == null) {
                    user.setPrograms(new ArrayList<>());
                }
                user.getPrograms().add(PersonalProgram);

                userRef.setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showAlertDialog("Program added to user successfully!");
                    } else {
                        showAlertDialog("Error updating user with new program.");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showAlertDialog(String message) {
        // Use mContext instead of "this"
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> { /* no-op */ });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

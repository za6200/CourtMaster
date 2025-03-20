package com.example.courtmaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainScreen extends AppCompatActivity {
    TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        name = findViewById(R.id.tVHelloName);
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference("Users").child(currentUid);
        refUsers.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.getValue(String.class);
                    if (userName != null && !userName.isEmpty()) {
                        name.setText("Hello: " + userName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

            public void FindCourts(View view) {
                Intent FindCourts = new Intent(MainScreen.this, Sign_Places.class);
                startActivity(FindCourts);
            }

            public void train(View view) {
                Intent Built_In = new Intent(MainScreen.this, Built_In_Programs.class);
                startActivity(Built_In);
            }

            public void Personal_Program(View view) {
                Intent PersonalTraining = new Intent(MainScreen.this, Personal_Program.class);
                startActivity(PersonalTraining);
            }

            public void Programs(View view) {
                Intent MyPrograms = new Intent(MainScreen.this, My_Programs.class);
                startActivity(MyPrograms);
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

        }

        return super.onOptionsItemSelected(item);
    }

}
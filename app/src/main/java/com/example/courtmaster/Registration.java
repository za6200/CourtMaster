package com.example.courtmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Registration extends AppCompatActivity {

    private static final String TAG = "Registration";

    // Firebase References
    private DatabaseReference refDatabase;
    private FirebaseAuth refAuth;

    // UI Elements
    private TextView tVtitle;
    private EditText eTname, eTemail, eTpass;
    private CheckBox cBstayconnect;
    private Button btn, btnSwitchToRegister;

    // Logic Fields
    private String name, email, password, uid;
    private Boolean stayConnect, isLoginMode;
    private SharedPreferences settings;

    private User userdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase references
        refAuth = FirebaseAuth.getInstance();
        refDatabase = FirebaseDatabase.getInstance().getReference();

        initViews();
        settings = getSharedPreferences("stayConnect", MODE_PRIVATE);

        // Default states
        stayConnect = false;
        isLoginMode = true;

        // Show login UI by default
        toggleMode(true);

        // Set click listener to toggle between Login and Register
        btnSwitchToRegister.setOnClickListener(this::switchToRegister);
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isChecked = settings.getBoolean("stayConnect", false);
        if (refAuth.getCurrentUser() != null && isChecked) {
            FBRef.getUser(refAuth.getCurrentUser());
            stayConnect = true;
            Intent si = new Intent(Registration.this, MainScreen.class);
            si.putExtra("isNewUser", false);
            startActivity(si);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stayConnect) finish();
    }

    private void initViews() {
        tVtitle = findViewById(R.id.tVtitle);
        eTname = findViewById(R.id.eTname);
        eTemail = findViewById(R.id.eTemail);
        eTpass = findViewById(R.id.eTpass);
        cBstayconnect = findViewById(R.id.cBstayconnect);
        btn = findViewById(R.id.login);
        btnSwitchToRegister = findViewById(R.id.RegisterBtn);
    }

    private void toggleMode(boolean isLogin) {
        isLoginMode = isLogin;
        tVtitle.setText(isLogin ? "Login" : "Register");
        eTname.setVisibility(isLogin ? View.INVISIBLE : View.VISIBLE);
        btn.setText(isLogin ? "Login" : "Register");
        btnSwitchToRegister.setText(isLogin ? "Switch to Register" : "Switch to Login");
    }

    public void onLoginOrRegisterClick(View view) {
        if (isLoginMode) {
            loginUser();
        } else {
            registerUser();
        }
    }

    private void loginUser() {
        email = eTemail.getText().toString();
        password = eTpass.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            showAlertDialog("Please fill in both email and password.");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showAlertDialog("Invalid email format.");
            return;
        }

        final ProgressDialog pd = ProgressDialog.show(this, "Login", "Connecting...", true);
        refAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        FBRef.getUser(refAuth.getCurrentUser());
                        Log.d(TAG, "signInWithEmail: success");
                        Toast.makeText(Registration.this, "Login Success!", Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("stayConnect", cBstayconnect.isChecked());
                        editor.apply();

                        Intent mainScreenIntent = new Intent(Registration.this, MainScreen.class);
                        startActivity(mainScreenIntent);
                    } else {
                        Log.d(TAG, "signInWithEmail: failure", task.getException());
                        showAlertDialog("E-mail or password are incorrect.");
                    }
                });
    }

    private void registerUser() {
        name = eTname.getText().toString();
        email = eTemail.getText().toString();
        password = eTpass.getText().toString();

        if (name.isEmpty()) {
            showAlertDialog("Name must be filled.");
            return;
        }
        if (email.isEmpty() || password.isEmpty()) {
            showAlertDialog("Please fill in both email and password.");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showAlertDialog("Invalid email format.");
            return;
        }
        if (password.length() < 6) {
            showAlertDialog("Password must be at least 6 characters long.");
            return;
        }

        final ProgressDialog pd = ProgressDialog.show(this, "Register", "Registering...", true);
        refAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = refAuth.getCurrentUser();
                        if (user != null) {
                            uid = user.getUid();
                            userdb = new User(uid, name, new ArrayList<>());

                            refDatabase.child("Users").child(uid).setValue(userdb)
                                    .addOnCompleteListener(saveTask -> {
                                        if (saveTask.isSuccessful()) {
                                            Toast.makeText(Registration.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "createUserWithEmail: success");

                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putBoolean("stayConnect", cBstayconnect.isChecked());
                                            editor.apply();

                                            startActivity(new Intent(Registration.this, MainScreen.class));
                                        } else {

                                            showAlertDialog("failed to add user");
                                        }
                                    });
                        }
                    } else {
                        showAlertDialog("User with this e-mail already exists!");
                    }
                });

    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {})
                .show();
    }

    public void switchToRegister(View view) {
        toggleMode(!isLoginMode); // Dynamically toggle between Login and Register
    }

    public void LoginOrRegister(View view) {
        if(btn.getText().toString().equals("Login"))
        {
            loginUser();
        }
        else
        {
            registerUser();
        }
    }
}

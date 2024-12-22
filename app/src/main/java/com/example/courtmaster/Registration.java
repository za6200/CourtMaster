package com.example.courtmaster;

import static com.example.courtmaster.FBRef.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Registration extends AppCompatActivity {

    private TextView tVtitle, tVregister;
    private EditText eTname, eTemail, eTpass;
    private CheckBox cBstayconnect;
    private Button btn;

    private String name, email, password, uid;
    private User userdb;
    private Boolean stayConnect, registered;
    private SharedPreferences settings;
    private final int REQUEST_CODE = 100;
    Intent si;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        si = new Intent(Registration.this, MainScreen.class);

        settings = getSharedPreferences("stayConnect", MODE_PRIVATE);
        initViews();
        stayConnect = false;
        registered = true;
        regoption();
    }

    private void initViews() {
        tVtitle = findViewById(R.id.tVtitle);
        eTname = findViewById(R.id.eTname);
        eTemail = findViewById(R.id.eTemail);
        eTpass = findViewById(R.id.eTpass);
        cBstayconnect = findViewById(R.id.cBstayconnect);
        tVregister = findViewById(R.id.tVregister);
        btn = findViewById(R.id.login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Boolean isChecked = settings.getBoolean("stayConnect", false);
        if (refAuth.getCurrentUser() != null && isChecked) {
            FBRef.getUser(refAuth.getCurrentUser());
            stayConnect = true;
            si.putExtra("isNewUser", false);
            startActivity(si);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stayConnect) finish();
    }

    private void regoption() {
        SpannableString ss = new SpannableString("Don't have an account?  Register here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                tVtitle.setText("Register");
                eTname.setVisibility(View.VISIBLE);
                btn.setText("Register");
                registered = false;
                logoption();
            }
        };
        ss.setSpan(span, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tVregister.setText(ss);
        tVregister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void logoption() {
        SpannableString ss = new SpannableString("Already have an account?  Login here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                tVtitle.setText("Login");
                eTname.setVisibility(View.INVISIBLE);
                btn.setText("Login");
                registered = true;
                regoption();
            }
        };
        ss.setSpan(span, 26, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tVregister.setText(ss);
        tVregister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void logorreg(View view) {
        if (registered) {
            email = eTemail.getText().toString();
            password = eTpass.getText().toString();

            if(email.equals("") || password.equals(""))
            {
                showAlertDialog("fields must be filled with a real Gmail and Password");
                return;
            }

            final ProgressDialog pd = ProgressDialog.show(this, "Login", "Connecting...", true);
            refAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                FBRef.getUser(refAuth.getCurrentUser());
                                Log.d("MainActivity", "signinUserWithEmail:success");
                                Toast.makeText(Registration.this, "Login Success", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("stayConnect", cBstayconnect.isChecked());
                                editor.apply();
                                Intent MainScreen = new Intent(Registration.this, MainScreen.class);
                                startActivity(MainScreen);
                            } else {
                                Log.d("MainActivity", "signinUserWithEmail:fail");
                                showAlertDialog("e-mail or password are wrong!");
                            }
                        }
                    });
        } else {
            name = eTname.getText().toString();
            email = eTemail.getText().toString();
            password = eTpass.getText().toString();
            if(name.equals(""))
            {
                showAlertDialog("Name must be filled");
                return;
            }

            final ProgressDialog pd = ProgressDialog.show(this, "Register", "Registering...", true);
            refAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                FirebaseUser user = refAuth.getCurrentUser();
                                FBRef.getUser(user);
                                uid = user.getUid();
                                List<Training_Program> userPrograms = new ArrayList<>();
                                userdb = new User(uid, name, userPrograms);
                                Toast.makeText(Registration.this, "Successful registration", Toast.LENGTH_SHORT).show();
                                Intent MainScreen = new Intent(Registration.this, MainScreen.class);
                                startActivity(MainScreen);
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                    Toast.makeText(Registration.this, "User with e-mail already exist!", Toast.LENGTH_SHORT).show();
                                else {
                                    Log.w("MainActivity", "createUserWithEmail:failure", task.getException());
                                    showAlertDialog("User create failed.");
                                }
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Removed activeYear logic from onActivityResult
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // You can add additional actions if needed
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

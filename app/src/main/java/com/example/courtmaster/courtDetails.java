package com.example.courtmaster;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class courtDetails extends AppCompatActivity {

    private EditText courtNameInput;
    private CheckBox netsCheckbox;
    private CheckBox roofCheckbox;
    private CheckBox halfFullCheckbox;

    private double latitude = 0.0;
    private double longitude = 0.0;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private List<Location> existingCourts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_details);

        // Initialize UI elements
        courtNameInput = findViewById(R.id.court_name);
        netsCheckbox = findViewById(R.id.checkbox_nets);
        roofCheckbox = findViewById(R.id.checkbox_roof);
        halfFullCheckbox = findViewById(R.id.checkbox_half_full);

        // Initialize fused location client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        // Load existing courts
        loadExistingCourts();


    }

    private void loadExistingCourts() {
        FBRef.refCourts.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DataSnapshot courtSnapshot : task.getResult().getChildren()) {
                    Court court = courtSnapshot.getValue(Court.class);
                    if (court != null) {
                        Location courtLocation = new Location("");
                        courtLocation.setLatitude(court.getLatitude());
                        courtLocation.setLongitude(court.getLongitude());
                        existingCourts.add(courtLocation);
                    }
                }
                Toast.makeText(this, "Loaded existing courts.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to load courts.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveCourt() {
        String courtName = courtNameInput.getText().toString();
        boolean hasNets = netsCheckbox.isChecked();
        boolean hasRoof = roofCheckbox.isChecked();
        boolean isHalfCourt = halfFullCheckbox.isChecked();

        if (courtName.isEmpty()) {
            showAlertDialog("Court name cannot be empty.");
            return;
        }

        // Check if court is near an existing court
        boolean isClose = false;
        Location currentLocation = new Location("");
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);

        for (Location existingLocation : existingCourts) {
            float distance = existingLocation.distanceTo(currentLocation);
            if (distance < 100) {
                showAlertDialog("There is already a court within " + distance + " meters!");
                isClose = true;
                break;
            }
        }

        if (isClose) return;

        // Construct facilities string
        StringBuilder facilities = new StringBuilder();
        facilities.append(isHalfCourt ? "Half court. " : "Full court. ");
        if (hasNets) facilities.append("\uD83D\uDDD1 ");
        if (hasRoof) facilities.append("⼧");

        String courtId = generateCourtID();

        Court court = new Court(courtId, latitude, longitude, courtName, facilities.toString(), hasNets, isHalfCourt, hasRoof);
        saveToFirebase(court);
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                });
    }

    private void saveToFirebase(Court court) {
        FBRef.refCourts.child(court.getCourtID()).setValue(court).addOnCompleteListener(task -> {
             if (task.isSuccessful()) {
                 Toast.makeText(courtDetails.this, "Court added successfully.", Toast.LENGTH_SHORT).show();
                 Intent si = new Intent(courtDetails.this, Sign_Places.class);
                 startActivity(si);
             } else {
                 showAlertDialog("Failed to add court. Please try again.");
             }
         });
    }

    private String generateCourtID() {
        return FBRef.refCourts.push().getKey();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }

    public void go_back(View view) {
        saveCourt();

    }
}

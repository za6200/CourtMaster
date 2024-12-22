package com.example.courtmaster;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.libraries.places.api.Places;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Sign_Places extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private GoogleMap mMap;
    private MapView mapView;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Intent GPS, Video, Registration;
    private ProgressDialog Retrivieving_Location;


    private static final String TAG = "Sign_Places";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_places);

        mapView = findViewById(R.id.mapView);
        if (mapView != null) {
            Bundle mapViewBundle = null;
            if (savedInstanceState != null) {
                mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
            }
            mapView.onCreate(mapViewBundle);
            mapView.getMapAsync(this);
        } else {
            Log.e(TAG, "mapView is null after findViewById");
        }

        Retrivieving_Location = ProgressDialog.show(this, "Map", "Retrieving location...", true);

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAuli4U0oXe5lMtKdYcQNdoVSPOeKDkfrM");
        }

        // Request Location Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE},
                    1);
        } else {
            getLocation();
            Retrivieving_Location.dismiss();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Enable My Location layer if permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        if (latitude != 0.0 && longitude != 0.0) {
            LatLng currentLocation = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14f));
        }
        Retrivieving_Location.dismiss();
    }

    private void getLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                // Request location updates
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (locationManager != null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, this);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 3, this);
                }

                // Get last known location
                fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                // Update map if it's ready
                                if (mMap != null) {
                                    LatLng currentLocation = new LatLng(latitude, longitude);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14f));
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // Handle location updates
    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    private void fetchBasketballPlaces() {
        if (latitude == 0.0 && longitude == 0.0) {
            showAlertDialog("Current location not available.");
            return;
        }

        double radiusInMeters = 5000.0;

        String apiKey = "AIzaSyAuli4U0oXe5lMtKdYcQNdoVSPOeKDkfrM";

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?location=" + latitude + "," + longitude
                + "&radius=" + radiusInMeters
                + "&keyword=basketball"
                + "&key=" + apiKey;

        Log.d(TAG, "Fetching basketball places with URL: " + url);
        final ProgressDialog pd = ProgressDialog.show(this, "Find Courts", "Searching...", true);
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                    try {
                        Log.d(TAG, "API Response: " + response.toString());

                        String status = response.getString("status");
                        if (!status.equals("OK")) {
                            String errorMessage = response.optString("error_message", "Unknown error");
                            Log.e(TAG, "API Error: " + status + " - " + errorMessage);
                            showAlertDialog("API Error: " + status + " - " + errorMessage);
                            return;
                        }

                        JSONArray results = response.getJSONArray("results");
                        Log.d(TAG, "Number of basketball places found: " + results.length());

                        mMap.clear(); // Clear existing markers
                        pd.dismiss();
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);

                            // Get place details
                            String placeName = place.getString("name");
                            JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");

                            // Add a marker for each place found
                            LatLng placeLatLng = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName));
                        }

                        Toast.makeText(this, "Nearby basketball places added to the map.", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: ", e);
                        showAlertDialog("Error parsing response data.");
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error: ", error);
                    showAlertDialog("Error fetching basketball places.");
                }
        );

        queue.add(jsonObjectRequest);
    }




    public void Find_Place(View view) {
        fetchBasketballPlaces();
    }

    // MapView lifecycle methods
    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null)
            mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        if (mapView != null)
            mapView.onSaveInstanceState(mapViewBundle);

        super.onSaveInstanceState(outState);
    }


    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean allGranted = true;
            for (int res : grantResults) {
                if (res != PackageManager.PERMISSION_GRANTED)
                    allGranted = false;
            }
            if (allGranted) {
                getLocation();
                if (mapView != null)
                    mapView.getMapAsync(this);
            } else {
                showAlertDialog("Location permissions are required.");
            }
        }
    }
    // Deprecated methods (can be left empty)
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Deprecated method
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        // Optionally handle provider enabled
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // Optionally handle provider disabled
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
}

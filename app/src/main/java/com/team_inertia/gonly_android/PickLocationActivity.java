package com.team_inertia.gonly_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class PickLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private Button confirmButton, myLocationButton;
    private TextView instructionText, selectedLocationText;

    private double selectedLat = 0;
    private double selectedLng = 0;
    private Marker currentPin = null;

    private FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.pickMapView);
        confirmButton = findViewById(R.id.confirmLocationButton);
        myLocationButton = findViewById(R.id.myLocationButton);
        instructionText = findViewById(R.id.pickInstructionText);
        selectedLocationText = findViewById(R.id.selectedLocationText);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Confirm button is disabled until user picks a location
        confirmButton.setEnabled(false);

        // ===== CONFIRM BUTTON =====
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLat == 0 && selectedLng == 0) {
                    Toast.makeText(PickLocationActivity.this,
                            "Tap on the map to pick a location", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send the selected location back to AddGemActivity
                Intent result = new Intent();
                result.putExtra("pickedLat", selectedLat);
                result.putExtra("pickedLng", selectedLng);
                setResult(RESULT_OK, result);
                finish();
            }
        });

        // ===== MY LOCATION BUTTON =====
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMyLocation();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Default to Northeast India
        LatLng defaultPos = new LatLng(25.5, 93.0);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPos, 7));

        // Try to go to user's location first
        goToMyLocation();

        // ===== WHEN USER TAPS ON MAP =====
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // Save the selected position
                selectedLat = latLng.latitude;
                selectedLng = latLng.longitude;

                // Remove old pin
                if (currentPin != null) {
                    currentPin.remove();
                }

                // Place new pin
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Your gem location")
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED));

                currentPin = googleMap.addMarker(markerOptions);

                // Show coordinates
                selectedLocationText.setText("📍 Lat: "
                        + String.format("%.6f", selectedLat)
                        + ", Lng: "
                        + String.format("%.6f", selectedLng));
                selectedLocationText.setVisibility(View.VISIBLE);

                // Enable confirm button
                confirmButton.setEnabled(true);
                confirmButton.setText("✅ Use This Location");

                instructionText.setText("Location selected! Tap again to change, or confirm below.");
            }
        });
    }

    private void goToMyLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && googleMap != null) {
                LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 14));
            }
        });
    }

    // MapView lifecycle
    @Override
    protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override
    protected void onPause() { super.onPause(); mapView.onPause(); }
    @Override
    protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override
    public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
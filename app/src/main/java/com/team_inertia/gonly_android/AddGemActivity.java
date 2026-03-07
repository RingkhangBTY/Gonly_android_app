package com.team_inertia.gonly_android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;
import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.api.ApiService;
import com.team_inertia.gonly_android.model.ApiResponse;
import com.team_inertia.gonly_android.model.GemRequest;
import com.team_inertia.gonly_android.model.GemResponse;
import com.team_inertia.gonly_android.util.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 200;
    private static final int PICK_LOCATION = 300;

    // === BASIC FIELDS ===
    private EditText nameInput, descInput, stateInput;
    private Spinner categorySpinner;
    private Button getLocationBtn, pickOnMapBtn, submitBtn;
    private TextView locationText;

    // === TOGGLE ===
    private Button toggleMoreBtn;
    private LinearLayout moreDetailsSection;
    private boolean moreDetailsVisible = false;

    // === EXTRA FIELDS ===
    private EditText townInput, tipsInput, howToReachInput, entryFeeInput;
    private EditText seasonWarningInput, safetyNoteInput, localContactInput;
    private Spinner difficultySpinner;
    private Spinner bestSeasonStartSpinner, bestSeasonEndSpinner;
    private CheckBox networkCheckbox;

    // === IMAGE ===
    private Button pickImageBtn;
    private ImageView selectedImagePreview;
    private TextView selectedImageName;
    private Uri selectedImageUri = null;

    // === LOCATION ===
    private double currentLat = 0, currentLng = 0;
    private String locationSourceType = "GPS_AUTO"; // tracks how location was chosen
    private FusedLocationProviderClient locationClient;

    private String[] categories = {"NATURE", "CULTURE", "FOOD", "ADVENTURE", "HERITAGE", "WILDLIFE"};
    private String[] difficulties = {"EASY", "MODERATE", "DIFFICULT", "EXTREME"};
    private String[] months = {
            "-- Select --", "January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ===== CHECK LOGIN =====
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login first to add a gem",
                    Toast.LENGTH_SHORT).show();
            try {
                startActivity(new Intent(this, LoginActivity.class));
            } catch (Exception e) { }
            finish();
            return;
        }

        setContentView(R.layout.activity_add_gem);

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        // ===== FIND BASIC VIEWS =====
        nameInput = findViewById(R.id.addGemName);
        descInput = findViewById(R.id.addGemDesc);
        stateInput = findViewById(R.id.addGemState);
        categorySpinner = findViewById(R.id.addGemCategory);
        getLocationBtn = findViewById(R.id.getLocationButton);
        pickOnMapBtn = findViewById(R.id.pickOnMapButton);
        submitBtn = findViewById(R.id.submitGemButton);
        locationText = findViewById(R.id.locationText);

        // ===== FIND TOGGLE VIEWS =====
        toggleMoreBtn = findViewById(R.id.toggleMoreDetails);
        moreDetailsSection = findViewById(R.id.moreDetailsSection);

        // ===== FIND EXTRA DETAIL VIEWS =====
        townInput = findViewById(R.id.addGemTown);
        tipsInput = findViewById(R.id.addGemTips);
        howToReachInput = findViewById(R.id.addGemHowToReach);
        entryFeeInput = findViewById(R.id.addGemEntryFee);
        seasonWarningInput = findViewById(R.id.addGemSeasonWarning);
        safetyNoteInput = findViewById(R.id.addGemSafetyNote);
        localContactInput = findViewById(R.id.addGemLocalContact);
        difficultySpinner = findViewById(R.id.addGemDifficulty);
        bestSeasonStartSpinner = findViewById(R.id.addGemSeasonStart);
        bestSeasonEndSpinner = findViewById(R.id.addGemSeasonEnd);
        networkCheckbox = findViewById(R.id.addGemNetworkAvailable);

        // ===== FIND IMAGE VIEWS =====
        pickImageBtn = findViewById(R.id.pickImageButton);
        selectedImagePreview = findViewById(R.id.selectedImagePreview);
        selectedImageName = findViewById(R.id.selectedImageName);

        // ===== SETUP SPINNERS =====
        categorySpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories));
        difficultySpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, difficulties));
        bestSeasonStartSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, months));
        bestSeasonEndSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, months));

        // ===== GPS LOCATION =====
        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGPSLocation();
            }
        });

        // ===== PICK ON MAP =====
        pickOnMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddGemActivity.this,
                        PickLocationActivity.class);
                startActivityForResult(intent, PICK_LOCATION);
            }
        });

        // ===== TOGGLE MORE DETAILS =====
        toggleMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreDetailsVisible = !moreDetailsVisible;
                if (moreDetailsVisible) {
                    moreDetailsSection.setVisibility(View.VISIBLE);
                    toggleMoreBtn.setText("▲ Hide Extra Details");
                } else {
                    moreDetailsSection.setVisibility(View.GONE);
                    toggleMoreBtn.setText("▼ Add More Details (optional)");
                }
            }
        });

        // ===== PICK IMAGE =====
        pickImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setType("image/*");
                startActivityForResult(pickIntent, PICK_IMAGE);
            }
        });

        // ===== SUBMIT =====
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitGem();
            }
        });
    }

    // ==================== GPS LOCATION ====================
    private void getGPSLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        getLocationBtn.setEnabled(false);
        getLocationBtn.setText("Getting location...");

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            getLocationBtn.setEnabled(true);
            getLocationBtn.setText("📍 Use My GPS Location");

            if (location != null) {
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                locationSourceType = "GPS_AUTO";
                locationText.setText("📍 GPS: " + String.format("%.6f", currentLat)
                        + ", " + String.format("%.6f", currentLng));
                locationText.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Couldn't get GPS location. Try 'Pick on Map' instead.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==================== HANDLE RESULTS FROM OTHER ACTIVITIES ====================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // === IMAGE PICKED ===
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                selectedImagePreview.setImageURI(selectedImageUri);
                selectedImagePreview.setVisibility(View.VISIBLE);
                selectedImageName.setText("✅ Image selected — will upload after gem is created");
                selectedImageName.setVisibility(View.VISIBLE);
                pickImageBtn.setText("📸 Change Image");
            }
        }

        // === LOCATION PICKED FROM MAP ===
        if (requestCode == PICK_LOCATION && resultCode == Activity.RESULT_OK && data != null) {
            currentLat = data.getDoubleExtra("pickedLat", 0);
            currentLng = data.getDoubleExtra("pickedLng", 0);
            locationSourceType = "MAP_PIN"; // user chose on map!

            locationText.setText("📌 Map Pin: " + String.format("%.6f", currentLat)
                    + ", " + String.format("%.6f", currentLng));
            locationText.setVisibility(View.VISIBLE);
        }
    }

    // ==================== SUBMIT GEM ====================
    private void submitGem() {
        String name = nameInput.getText().toString().trim();
        String desc = descInput.getText().toString().trim();
        String state = stateInput.getText().toString().trim();

        if (name.isEmpty()) { nameInput.setError("Name is required"); return; }
        if (desc.isEmpty()) { descInput.setError("Description is required"); return; }
        if (state.isEmpty()) { stateInput.setError("State is required"); return; }
        if (currentLat == 0 && currentLng == 0) {
            Toast.makeText(this, "Please set location first (GPS or Map)!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        GemRequest request = new GemRequest();
        request.setName(name);
        request.setDescription(desc);
        request.setLatitude(currentLat);
        request.setLongitude(currentLng);
        request.setLocationSource(locationSourceType); // GPS_AUTO or MAP_PIN
        request.setCategory(categories[categorySpinner.getSelectedItemPosition()]);
        request.setState(state);

        // Extra fields
        String town = townInput.getText().toString().trim();
        String tips = tipsInput.getText().toString().trim();
        String howToReach = howToReachInput.getText().toString().trim();
        String entryFee = entryFeeInput.getText().toString().trim();
        String seasonWarning = seasonWarningInput.getText().toString().trim();
        String safetyNote = safetyNoteInput.getText().toString().trim();
        String localContact = localContactInput.getText().toString().trim();

        if (!town.isEmpty()) request.setNearestTown(town);
        if (!tips.isEmpty()) request.setTravelTips(tips);
        if (!howToReach.isEmpty()) request.setHowToReach(howToReach);
        if (!entryFee.isEmpty()) request.setEntryFee(entryFee);
        if (!seasonWarning.isEmpty()) request.setSeasonWarning(seasonWarning);
        if (!safetyNote.isEmpty()) request.setSafetyNote(safetyNote);
        if (!localContact.isEmpty()) request.setLocalContact(localContact);

        request.setDifficultyLevel(difficulties[difficultySpinner.getSelectedItemPosition()]);

        int startMonth = bestSeasonStartSpinner.getSelectedItemPosition();
        int endMonth = bestSeasonEndSpinner.getSelectedItemPosition();
        if (startMonth > 0) request.setBestSeasonStart(startMonth);
        if (endMonth > 0) request.setBestSeasonEnd(endMonth);

        request.setNetworkAvailable(networkCheckbox.isChecked());

        submitBtn.setEnabled(false);
        submitBtn.setText("Submitting...");

        ApiService api = ApiClient.getApiService(this);
        api.createGem(request).enqueue(new Callback<GemResponse>() {
            @Override
            public void onResponse(Call<GemResponse> call, Response<GemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    long newGemId = response.body().getId();
                    if (selectedImageUri != null) {
                        uploadImageToGem(newGemId);
                    } else {
                        Toast.makeText(AddGemActivity.this,
                                "Gem created! 🎉", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    submitBtn.setEnabled(true);
                    submitBtn.setText("Submit Gem");
                    Toast.makeText(AddGemActivity.this,
                            "Failed to create gem", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GemResponse> call, Throwable t) {
                submitBtn.setEnabled(true);
                submitBtn.setText("Submit Gem");
                Toast.makeText(AddGemActivity.this,
                        "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==================== UPLOAD IMAGE ====================
    private void uploadImageToGem(long gemId) {
        submitBtn.setText("Uploading image...");
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            if (inputStream == null) {
                Toast.makeText(this, "Gem created but image upload failed",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap == null) {
                Toast.makeText(this, "Gem created! (could not read image)",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            byte[] bytes = out.toByteArray();
            out.close();
            bitmap.recycle();

            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("image/jpeg"), bytes);
            MultipartBody.Part part = MultipartBody.Part.createFormData(
                    "image", "photo.jpg", requestBody);

            ApiService api = ApiClient.getApiService(this);
            api.uploadGemImage(gemId, part).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call,
                                       Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddGemActivity.this,
                                "Gem created with photo! 🎉📸", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddGemActivity.this,
                                "Gem created! (image upload failed)", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(AddGemActivity.this,
                            "Gem created! (image upload failed)", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Gem created! (image error)", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
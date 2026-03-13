package com.team_inertia.gonly_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team_inertia.gonly_android.adapter.ReviewAdapter;
import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.api.ApiService;
import com.team_inertia.gonly_android.model.*;
import com.team_inertia.gonly_android.util.SessionManager;

import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GemDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    private long gemId;
    private SessionManager sessionManager;
    private boolean isInfoExpanded = false , isActionExpanded = false , isReportSuggestionExpanded = false;

    // Store gem location for Google Maps button
    private double gemLat = 0;
    private double gemLng = 0;
    private String gemName = "";

    // Basic info views
    private TextView nameText, descText, stateText, categoryText, ratingText;
    private TextView photoCountText;
    private LinearLayout imageGalleryContainer;

    // Extra info views (inside collapsible container)
    private LinearLayout extraInfoContainer , reportSuggestionsContainer, actionButtonsContainer;
    private TextView tipsText, howToReachText, difficultyText;
    private TextView entryFeeText, safetyNoteText, localContactText, nearestTownText, networkText;

    // Buttons
    private Button toggleInfoBtn , toggleActionBtn , bookmarkBtn, uploadImageBtn, submitReviewBtn,
            openMapBtn , reportGemButton ;

    // Review views
    private EditText reviewComment;
    private RatingBar reviewRatingBar;
    private RecyclerView reviewsList;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gem_detail);

        sessionManager = new SessionManager(this);
        gemId = getIntent().getLongExtra("gemId", -1);

        initializeViews(); // set up all view references
        setUpListeners(); // set up all button click listeners


        // Setup reviews list
        reviewAdapter = new ReviewAdapter();
        reviewsList.setLayoutManager(new LinearLayoutManager(this));
        reviewsList.setAdapter(reviewAdapter);

        // Load data
        loadGemDetail();
        loadRealImages();
        loadReviews();
    }

    private void safeGoToLogin() {
        try {
            startActivity(new Intent(GemDetailActivity.this, LoginActivity.class));
        } catch (Exception e) {
            Toast.makeText(this,
                    "Please go to Profile tab to login", Toast.LENGTH_LONG).show();
        }
    }

    // ==================== GOOGLE MAPS ====================
    private void openInGoogleMaps() {
        try {
            String uriString = "geo:" + gemLat + "," + gemLng
                    + "?q=" + gemLat + "," + gemLng
                    + "(" + gemName + ")";
            Uri gmmIntentUri = Uri.parse(uriString);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                String browserUrl = "https://www.google.com/maps/search/?api=1&query="
                        + gemLat + "," + gemLng;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(browserUrl)));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Could not open maps", Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== LOAD GEM DETAIL ====================
    private void loadGemDetail() {
        ApiService api = ApiClient.getApiService(this);
        api.getGemById(gemId).enqueue(new Callback<GemResponse>() {
            @Override
            public void onResponse(Call<GemResponse> call, Response<GemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GemResponse gem = response.body();

                    if (gem.getLatitude() != null)  gemLat  = gem.getLatitude();
                    if (gem.getLongitude() != null)  gemLng  = gem.getLongitude();
                    if (gem.getName() != null)       gemName = gem.getName();

                    nameText.setText(gem.getName());
                    descText.setText(gem.getDescription());
                    stateText.setText("📍 " + gem.getState());
                    categoryText.setText("🏷️ " + gem.getCategory());

                    if (gem.getAvgRating() != null) {
                        ratingText.setText("⭐ " + gem.getAvgRating()
                                + " (" + gem.getReviewCount() + " reviews)");
                    } else {
                        ratingText.setText("⭐ No ratings yet");
                    }

                    // ── Extra collapsible fields ──
                    showOrHide(tipsText,         gem.getTravelTips(),     "💡 Tips: ");
                    showOrHide(howToReachText,   gem.getHowToReach(),     "🚗 How to reach: ");
                    showOrHide(difficultyText,   gem.getDifficultyLevel(),"⛰️ Difficulty: ");
                    showOrHide(entryFeeText,     gem.getEntryFee(),       "🎟️ Entry Fee: ");
                    showOrHide(safetyNoteText,   gem.getSafetyNote(),     "⚠️ Safety: ");
                    showOrHide(localContactText, gem.getLocalContact(),   "📞 Local Contact: ");
                    showOrHide(nearestTownText,  gem.getNearestTown(),    "🏘️ Nearest Town: ");

                    if (gem.getNetworkAvailable() != null) {
                        networkText.setText(gem.getNetworkAvailable()
                                ? "📶 Network: Available"
                                : "📵 Network: Not Available");
                        networkText.setVisibility(View.VISIBLE);
                    } else {
                        networkText.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<GemResponse> call, Throwable t) {
                Toast.makeText(GemDetailActivity.this,
                        "Failed to load gem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Simple helper to show or hide a text field
    private void showOrHide(TextView textView, String value, String prefix) {
        if (value != null && !value.isEmpty()) {
            textView.setText(prefix + value);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    // ==================== LOAD IMAGES ====================
    private void loadRealImages() {
        ApiService api = ApiClient.getApiService(this);
        api.getAllGemImages(gemId).enqueue(new Callback<List<GemImageData>>() {
            @Override
            public void onResponse(Call<List<GemImageData>> call,
                                   Response<List<GemImageData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    buildImageGallery(response.body());
                } else {
                    showNoPhotosPlaceholder();
                }
            }

            @Override
            public void onFailure(Call<List<GemImageData>> call, Throwable t) {
                showNoPhotosPlaceholder();
            }
        });
    }

    private void buildImageGallery(List<GemImageData> images) {
        imageGalleryContainer.removeAllViews();

        if (images == null || images.size() == 0) {
            showNoPhotosPlaceholder();
            return;
        }

        int count = images.size();
        photoCountText.setText(count == 1
                ? "📸 1 photo"
                : "📸 " + count + " photos — scroll to see all →");

        for (GemImageData imageData : images) {
            String base64String = imageData.getImageBase64();
            if (base64String == null || base64String.isEmpty()) continue;

            byte[] imageBytes = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            if (bitmap == null) continue;

            ImageView imageView = new ImageView(GemDetailActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(300), dpToPx(250));
            params.setMargins(0, 0, dpToPx(8), 0);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(bitmap);

            final String uploaderName = imageData.getUploadedByName();
            imageView.setOnClickListener(v -> {
                String msg = (uploaderName != null && !uploaderName.isEmpty())
                        ? "📸 Uploaded by: " + uploaderName
                        : "Photo";
                Toast.makeText(GemDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            });

            imageGalleryContainer.addView(imageView);
        }

        if (imageGalleryContainer.getChildCount() == 0) {
            showNoPhotosPlaceholder();
        }
    }

    private void showNoPhotosPlaceholder() {
        imageGalleryContainer.removeAllViews();
        photoCountText.setText("📸 No photos yet — be the first to add one!");

        ImageView placeholder = new ImageView(this);
        placeholder.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(200)));
        placeholder.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        placeholder.setImageResource(R.drawable.ic_launcher_background);
        imageGalleryContainer.addView(placeholder);
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // ==================== REVIEWS ====================
    private void loadReviews() {
        ApiService api = ApiClient.getApiService(this);
        api.getReviews(gemId).enqueue(new Callback<List<ReviewResponse>>() {
            @Override
            public void onResponse(Call<List<ReviewResponse>> call,
                                   Response<List<ReviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviewAdapter.setReviews(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ReviewResponse>> call, Throwable t) { }
        });
    }

    // ==================== BOOKMARK ====================
    private void toggleBookmark() {
        ApiService api = ApiClient.getApiService(this);
        api.toggleBookmark(gemId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(GemDetailActivity.this,
                            response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GemDetailActivity.this,
                            "Bookmark failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(GemDetailActivity.this,
                        "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==================== SUBMIT REVIEW ====================
    private void submitReview() {
        int rating = (int) reviewRatingBar.getRating();
        String comment = reviewComment.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }
        if (comment.isEmpty()) {
            reviewComment.setError("Write a comment");
            return;
        }

        ApiService api = ApiClient.getApiService(this);
        ReviewRequest request = new ReviewRequest(rating, comment, null);

        api.addReview(gemId, request).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call,
                                   Response<ReviewResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GemDetailActivity.this,
                            "Review added! ⭐", Toast.LENGTH_SHORT).show();
                    reviewComment.setText("");
                    reviewRatingBar.setRating(0);
                    loadReviews();
                    loadGemDetail();
                } else {
                    Toast.makeText(GemDetailActivity.this,
                            "Failed to add review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Toast.makeText(GemDetailActivity.this,
                        "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==================== IMAGE UPLOAD ====================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Toast.makeText(this, "Could not read image", Toast.LENGTH_SHORT).show();
                return;
            }
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), bytes);
            MultipartBody.Part part = MultipartBody.Part.createFormData(
                    "image", "photo.jpg", requestBody);

            ApiService api = ApiClient.getApiService(this);
            api.uploadGemImage(gemId, part).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call,
                                       Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(GemDetailActivity.this,
                                "Image uploaded! 📸", Toast.LENGTH_SHORT).show();
                        loadRealImages();
                    } else {
                        Toast.makeText(GemDetailActivity.this,
                                "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(GemDetailActivity.this,
                            "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpListeners(){
        // ==================== TOGGLE INFO ====================
        toggleInfoBtn.setOnClickListener(v -> {
            isInfoExpanded = !isInfoExpanded;
            extraInfoContainer.setVisibility(isInfoExpanded ? View.VISIBLE : View.GONE);
            toggleInfoBtn.setText(isInfoExpanded ? "ℹ️ Show Less ▲" : "ℹ️ Show More Info ▼");
        });

        // ==================== TOGGLE ACTION BUTTON ====================
        toggleActionBtn.setOnClickListener(view -> {
            isActionExpanded = !isActionExpanded;
            isReportSuggestionExpanded = !isReportSuggestionExpanded;

            actionButtonsContainer.setVisibility(isActionExpanded? View.VISIBLE : View.GONE);
            reportSuggestionsContainer.setVisibility(isReportSuggestionExpanded ? View.VISIBLE : View.GONE);
            toggleActionBtn.setText(isActionExpanded ? "⚡ Hide Action Buttons ▲" : "⚡ Show Action Buttons ▼");
        });


        // ==================== OPEN IN GOOGLE MAPS ====================
        openMapBtn.setOnClickListener(v -> openInGoogleMaps());

        // ==================== BOOKMARK ====================
        bookmarkBtn.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(GemDetailActivity.this,
                        "Please login first", Toast.LENGTH_SHORT).show();
                safeGoToLogin();
                return;
            }
            toggleBookmark();
        });

        // ==================== UPLOAD IMAGE ====================
        uploadImageBtn.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(GemDetailActivity.this,
                        "Please login first", Toast.LENGTH_SHORT).show();
                safeGoToLogin();
                return;
            }
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType("image/*");
            startActivityForResult(pickIntent, PICK_IMAGE);
        });

        // ==================== SUBMIT REVIEW ====================
        submitReviewBtn.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(GemDetailActivity.this,
                        "Please login first", Toast.LENGTH_SHORT).show();
                safeGoToLogin();
                return;
            }
            submitReview();
        });

        // goto report page
        reportGemButton.setOnClickListener(view -> {
            if (!sessionManager.isLoggedIn()){
                Toast.makeText(GemDetailActivity.this,
                        "Please login first", Toast.LENGTH_SHORT).show();
                safeGoToLogin();
                return;
            }

            Intent reportIntent = new Intent(GemDetailActivity.this,ReportActivity.class);
            reportIntent.putExtra("gemId",gemId);
            startActivity(reportIntent);
            finish();
        });
    }

    private void initializeViews() {
        // ── Basic info views ──
        nameText     = findViewById(R.id.detailName);
        descText     = findViewById(R.id.detailDescription);
        stateText    = findViewById(R.id.detailState);
        categoryText = findViewById(R.id.detailCategory);
        ratingText   = findViewById(R.id.detailRating);
        photoCountText       = findViewById(R.id.photoCountText);
        imageGalleryContainer = findViewById(R.id.imageGalleryContainer);

        // ── Toggle + collapsible container ──
        toggleInfoBtn      = findViewById(R.id.toggleInfoButton);
        toggleActionBtn   = findViewById(R.id.toggleActionButton);
        extraInfoContainer = findViewById(R.id.extraInfoContainer);

        // ── Extra info views (inside collapsible) ──
        tipsText        = findViewById(R.id.detailTips);
        howToReachText  = findViewById(R.id.detailHowToReach);
        difficultyText  = findViewById(R.id.detailDifficulty);
        entryFeeText    = findViewById(R.id.detailEntryFee);
        safetyNoteText  = findViewById(R.id.detailSafetyNote);
        localContactText = findViewById(R.id.detailLocalContact);
        nearestTownText = findViewById(R.id.detailNearestTown);
        networkText     = findViewById(R.id.detailNetwork);

        // ── Buttons ──
        openMapBtn    = findViewById(R.id.openMapButton);
        bookmarkBtn   = findViewById(R.id.bookmarkButton);
        uploadImageBtn = findViewById(R.id.uploadImageButton);
        submitReviewBtn = findViewById(R.id.submitReviewButton);
        reportGemButton = findViewById(R.id.reportIssueButton);

        // ── Review views ──
        reviewComment   = findViewById(R.id.reviewCommentInput);
        reviewRatingBar = findViewById(R.id.reviewRatingBar);
        reviewsList     = findViewById(R.id.reviewsRecycler);


        actionButtonsContainer = findViewById(R.id.bookmarkUploadContainer);
        reportSuggestionsContainer = findViewById(R.id.reportSuggestionsContainer);
    }
}
package com.team_inertia.gonly_android.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.team_inertia.gonly_android.AddGemActivity;
import com.team_inertia.gonly_android.GemDetailActivity;
import com.team_inertia.gonly_android.R;
import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.api.ApiService;
import com.team_inertia.gonly_android.model.GemResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private EditText searchBar;
    private Button searchButton, addGemButton;
    private TextView resultCountText;
    private FusedLocationProviderClient locationClient;

    // Store all gems so we can filter locally
    private List<GemResponse> allGems = new ArrayList<>();

    // Map from marker ID to gem ID, so we know which gem was tapped
    private Map<String, Long> markerToGemId = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        searchBar = view.findViewById(R.id.mapSearchBar);
        searchButton = view.findViewById(R.id.mapSearchButton);
        addGemButton = view.findViewById(R.id.addGemFab);
        resultCountText = view.findViewById(R.id.mapResultCount);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        locationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // ===== SEARCH BUTTON =====
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBar.getText().toString().trim();
                if (query.isEmpty()) {
                    // Show all gems again
                    showGemsOnMap(allGems);
                    resultCountText.setVisibility(View.GONE);
                } else {
                    searchGems(query);
                }
            }
        });

        // ===== CLEAR SEARCH when text is empty =====
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    showGemsOnMap(allGems);
                    resultCountText.setVisibility(View.GONE);
                }
            }
        });

        // ===== ADD GEM BUTTON =====
        addGemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), AddGemActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Set default camera to Northeast India
        LatLng northeastIndia = new LatLng(25.5, 93.0);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(northeastIndia, 6));

        // Enable my location button if permission granted
        enableMyLocation();

        // When user taps on a marker info window → go to gem detail
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                String markerId = marker.getId();
                Long gemId = markerToGemId.get(markerId);
                if (gemId != null) {
                    Intent intent = new Intent(requireActivity(), GemDetailActivity.class);
                    intent.putExtra("gemId", gemId.longValue());
                    startActivity(intent);
                }
            }
        });

        // Load all gems
        loadAllGems();
    }

    // ==================== LOAD ALL GEMS ====================
    private void loadAllGems() {
        ApiService api = ApiClient.getApiService(requireActivity());
        api.getAllGems().enqueue(new Callback<List<GemResponse>>() {
            @Override
            public void onResponse(Call<List<GemResponse>> call,
                                   Response<List<GemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allGems = response.body();
                    showGemsOnMap(allGems);
                }
            }

            @Override
            public void onFailure(Call<List<GemResponse>> call, Throwable t) {
                Toast.makeText(requireActivity(),
                        "Failed to load gems", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==================== SEARCH GEMS (API call) ====================
    private void searchGems(String query) {
        ApiService api = ApiClient.getApiService(requireActivity());
        api.searchGems(query).enqueue(new Callback<List<GemResponse>>() {
            @Override
            public void onResponse(Call<List<GemResponse>> call,
                                   Response<List<GemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GemResponse> results = response.body();
                    showGemsOnMap(results);

                    // Show result count
                    if (results.isEmpty()) {
                        resultCountText.setText("No gems found for \"" + query + "\"");
                    } else {
                        resultCountText.setText(results.size() + " gem(s) found");
                    }
                    resultCountText.setVisibility(View.VISIBLE);

                    // Zoom to first result
                    if (!results.isEmpty()) {
                        GemResponse first = results.get(0);
                        if (first.getLatitude() != null && first.getLongitude() != null) {
                            LatLng pos = new LatLng(first.getLatitude(), first.getLongitude());
                            googleMap.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(pos, 10));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GemResponse>> call, Throwable t) {
                Toast.makeText(requireActivity(),
                        "Search failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==================== SHOW GEMS AS PINS ON MAP ====================
    private void showGemsOnMap(List<GemResponse> gems) {
        if (googleMap == null) return;

        // Clear old markers
        googleMap.clear();
        markerToGemId.clear();

        for (int i = 0; i < gems.size(); i++) {
            GemResponse gem = gems.get(i);

            if (gem.getLatitude() == null || gem.getLongitude() == null) {
                continue; // skip gems without location
            }

            LatLng position = new LatLng(gem.getLatitude(), gem.getLongitude());

            // Choose marker color based on category
            float markerColor = getMarkerColor(gem.getCategory());

            // Build the info shown when marker is tapped
            String snippet = "📍 " + gem.getState();
            if (gem.getCategory() != null) {
                snippet = snippet + " | 🏷️ " + gem.getCategory();
            }
            if (gem.getAvgRating() != null) {
                snippet = snippet + " | ⭐ " + gem.getAvgRating();
            }
            snippet = snippet + "\nTap here to view details";

            // Add marker to map
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(gem.getName())
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor));

            Marker marker = googleMap.addMarker(markerOptions);

            // Save the connection: marker ID → gem ID
            if (marker != null) {
                markerToGemId.put(marker.getId(), gem.getId());
            }
        }
    }

    // ==================== MARKER COLORS BY CATEGORY ====================
    private float getMarkerColor(String category) {
        if (category == null) return BitmapDescriptorFactory.HUE_RED;

        // Each category gets a different color pin
        if (category.equals("NATURE")) return BitmapDescriptorFactory.HUE_GREEN;
        if (category.equals("CULTURE")) return BitmapDescriptorFactory.HUE_VIOLET;
        if (category.equals("FOOD")) return BitmapDescriptorFactory.HUE_ORANGE;
        if (category.equals("ADVENTURE")) return BitmapDescriptorFactory.HUE_BLUE;
        if (category.equals("HERITAGE")) return BitmapDescriptorFactory.HUE_YELLOW;
        if (category.equals("WILDLIFE")) return BitmapDescriptorFactory.HUE_CYAN;

        return BitmapDescriptorFactory.HUE_RED;
    }

    // ==================== MY LOCATION ====================
    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

            // Move camera to user's location
            locationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(myPos, 8));
                }
            });
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // ==================== REFRESH when coming back ====================
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        // Reload gems in case user added a new one
        if (googleMap != null) {
            loadAllGems();
        }
    }

    @Override
    public void onPause() { super.onPause(); mapView.onPause(); }
    @Override
    public void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override
    public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
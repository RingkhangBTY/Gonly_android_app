package com.team_inertia.gonly_android.fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.team_inertia.gonly_android.R;
import com.team_inertia.gonly_android.adapter.GemAdapter;
import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.api.ApiService;
import com.team_inertia.gonly_android.model.GemResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private EditText searchInput;
    private Button searchBtn;
    private ChipGroup categoryChips;
    private EditText stateFilterInput;
    private Button stateFilterBtn;
    private RecyclerView resultsRecycler;
    private GemAdapter gemAdapter;
    private TextView noResultsText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        searchBtn = view.findViewById(R.id.searchButton);
        categoryChips = view.findViewById(R.id.categoryChips);
        stateFilterInput = view.findViewById(R.id.stateFilterInput);
        stateFilterBtn = view.findViewById(R.id.stateFilterButton);
        resultsRecycler = view.findViewById(R.id.searchResults);
        noResultsText = view.findViewById(R.id.noResultsText);

        gemAdapter = new GemAdapter();
        resultsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        resultsRecycler.setAdapter(gemAdapter);

        // Search button
        searchBtn.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                doSearch(query);
            }
        });

        // Category chips
        String[] cats = {"NATURE", "CULTURE", "FOOD", "ADVENTURE", "HERITAGE", "WILDLIFE"};
        for (String cat : cats) {
            Chip chip = new Chip(requireActivity());
            chip.setText(cat);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> filterByCategory(cat));
            categoryChips.addView(chip);
        }

        // State filter
        stateFilterBtn.setOnClickListener(v -> {
            String state = stateFilterInput.getText().toString().trim();
            if (!state.isEmpty()) {
                filterByState(state);
            }
        });

        // Load all gems initially
        loadAllGems();

        return view;
    }

    private void loadAllGems() {
        ApiService api = ApiClient.getApiService(requireActivity());
        api.getAllGems().enqueue(new Callback<List<GemResponse>>() {
            @Override
            public void onResponse(Call<List<GemResponse>> call, Response<List<GemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showResults(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<GemResponse>> call, Throwable t) { }
        });
    }

    private void doSearch(String query) {
        ApiService api = ApiClient.getApiService(requireActivity());
        api.searchGems(query).enqueue(new Callback<List<GemResponse>>() {
            @Override
            public void onResponse(Call<List<GemResponse>> call, Response<List<GemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showResults(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<GemResponse>> call, Throwable t) {
                Toast.makeText(requireActivity(), "Search failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByCategory(String category) {
        ApiService api = ApiClient.getApiService(requireActivity());
        api.filterByCategory(category).enqueue(new Callback<List<GemResponse>>() {
            @Override
            public void onResponse(Call<List<GemResponse>> call, Response<List<GemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showResults(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<GemResponse>> call, Throwable t) { }
        });
    }

    private void filterByState(String state) {
        ApiService api = ApiClient.getApiService(requireActivity());
        api.filterByState(state).enqueue(new Callback<List<GemResponse>>() {
            @Override
            public void onResponse(Call<List<GemResponse>> call, Response<List<GemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showResults(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<GemResponse>> call, Throwable t) { }
        });
    }

    private void showResults(List<GemResponse> gems) {
        gemAdapter.setGems(gems);
        if (gems.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE);
        } else {
            noResultsText.setVisibility(View.GONE);
        }
    }
}
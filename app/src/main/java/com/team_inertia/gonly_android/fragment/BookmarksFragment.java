package com.team_inertia.gonly_android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team_inertia.gonly_android.LoginActivity;
import com.team_inertia.gonly_android.R;
import com.team_inertia.gonly_android.adapter.GemAdapter;
import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.api.ApiService;
import com.team_inertia.gonly_android.model.GemResponse;
import com.team_inertia.gonly_android.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarksFragment extends Fragment {

    private RecyclerView bookmarksList;
    private GemAdapter gemAdapter;
    private TextView emptyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        bookmarksList = view.findViewById(R.id.bookmarksRecycler);
        emptyText = view.findViewById(R.id.emptyBookmarksText);

        gemAdapter = new GemAdapter();
        bookmarksList.setLayoutManager(new LinearLayoutManager(requireActivity()));
        bookmarksList.setAdapter(gemAdapter);

        // Check if logged in
        SessionManager session = new SessionManager(requireActivity());
        if (!session.isLoggedIn()) {
            emptyText.setText("Please login to see your bookmarks");
            emptyText.setVisibility(View.VISIBLE);
            bookmarksList.setVisibility(View.GONE);

            // Make the text clickable to go to login
            emptyText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                }
            });
        } else {
            loadBookmarks();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SessionManager session = new SessionManager(requireActivity());
        if (session.isLoggedIn()) {
            loadBookmarks();
        }
    }

    private void loadBookmarks() {
        ApiService api = ApiClient.getApiService(requireActivity());
        api.getMyBookmarks().enqueue(new Callback<List<GemResponse>>() {
            @Override
            public void onResponse(Call<List<GemResponse>> call, Response<List<GemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GemResponse> bookmarks = response.body();
                    gemAdapter.setGems(bookmarks);
                    if (bookmarks.isEmpty()) {
                        emptyText.setText("No bookmarks yet. Start exploring!");
                        emptyText.setVisibility(View.VISIBLE);
                    } else {
                        emptyText.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GemResponse>> call, Throwable t) { }
        });
    }
}
package com.team_inertia.gonly_android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team_inertia.gonly_android.AddEventActivity;
import com.team_inertia.gonly_android.R;
import com.team_inertia.gonly_android.adapter.EventAdapter;
import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.api.ApiService;
import com.team_inertia.gonly_android.model.EventResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsFragment extends Fragment {

    private RecyclerView eventsRecycler;
    private EventAdapter eventAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        eventsRecycler = view.findViewById(R.id.eventsRecycler);
        eventAdapter = new EventAdapter();
        eventsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        eventsRecycler.setAdapter(eventAdapter);

        view.findViewById(R.id.addEventFab).setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), AddEventActivity.class));
        });

        loadEvents();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        ApiService api = ApiClient.getApiService(requireActivity());
        api.getAllEvents().enqueue(new Callback<List<EventResponse>>() {
            @Override
            public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventAdapter.setEvents(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<EventResponse>> call, Throwable t) { }
        });
    }
}
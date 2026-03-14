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
import com.team_inertia.gonly_android.RegisterActivity;
import com.team_inertia.gonly_android.adapter.GemAdapter;
import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.api.ApiService;
import com.team_inertia.gonly_android.model.ApiResponse;
import com.team_inertia.gonly_android.model.GemResponse;
import com.team_inertia.gonly_android.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SessionManager session = new SessionManager(requireActivity());

        // ===== NOT LOGGED IN → show login/register screen =====
        if (!session.isLoggedIn()) {
            View view = inflater.inflate(R.layout.fragment_profile_guest, container, false);

            Button loginBtn = view.findViewById(R.id.guestLoginButton);
            Button registerBtn = view.findViewById(R.id.guestRegisterButton);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                }
            });

            registerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(requireActivity(), RegisterActivity.class));
                }
            });

            return view;
        }

        // ===== LOGGED IN → show profile =====
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView nameText = view.findViewById(R.id.profileName);
        TextView emailText = view.findViewById(R.id.profileEmail);
        Button logoutBtn = view.findViewById(R.id.logoutButton);
        RecyclerView myGemsRecycler = view.findViewById(R.id.myGemsRecycler);

        // FIX: Use array holder to avoid "variable might not be initialized" error.
        // Java lambdas cannot reference a local variable before it is fully assigned.
        // The array wrapper is effectively final but its contents can be set after creation.
        final GemAdapter[] adapterHolder = {null};

        adapterHolder[0] = new GemAdapter((gem, position) -> {
            ApiService api2 = ApiClient.getApiService(requireActivity());

            api2.deleteGem(gem.getId()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        adapterHolder[0].removeGem(position);
                        Toast.makeText(requireActivity(),
                                "Gem deleted successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(requireActivity(),
                            "Failed to delete gem",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        GemAdapter gemAdapter = adapterHolder[0];

        myGemsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        myGemsRecycler.setAdapter(gemAdapter);

        nameText.setText(session.getFullName());
        emailText.setText(session.getEmail());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logout();
                ApiClient.resetClient();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new ProfileFragment())
                        .commit();
            }
        });

        // Load my gems
        ApiService api = ApiClient.getApiService(requireActivity());
        api.getMyGems().enqueue(new Callback<List<GemResponse>>() {
            @Override
            public void onResponse(Call<List<GemResponse>> call, Response<List<GemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    gemAdapter.setGems(response.body());
                }

            }

            @Override
            public void onFailure(Call<List<GemResponse>> call, Throwable t) { }
        });

        return view;
    }
}
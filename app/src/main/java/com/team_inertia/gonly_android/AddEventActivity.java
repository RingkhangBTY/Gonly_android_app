package com.team_inertia.gonly_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.api.ApiService;
import com.team_inertia.gonly_android.model.EventRequest;
import com.team_inertia.gonly_android.model.EventResponse;
import com.team_inertia.gonly_android.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventActivity extends AppCompatActivity {

    private EditText titleInput, descInput, stateInput, startDateInput, endDateInput;
    private Spinner typeSpinner;
    private Button submitBtn;
    private String[] eventTypes = {"FESTIVAL", "FAIR", "MUSIC", "CULTURAL", "MARKET", "SPORTS", "RELIGIOUS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ===== CHECK LOGIN FIRST =====
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login first to add an event", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_add_event);

        titleInput = findViewById(R.id.addEventTitle);
        descInput = findViewById(R.id.addEventDesc);
        stateInput = findViewById(R.id.addEventState);
        startDateInput = findViewById(R.id.addEventStartDate);
        endDateInput = findViewById(R.id.addEventEndDate);
        typeSpinner = findViewById(R.id.addEventType);
        submitBtn = findViewById(R.id.submitEventButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, eventTypes);
        typeSpinner.setAdapter(adapter);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEvent();
            }
        });
    }

    private void submitEvent() {
        String title = titleInput.getText().toString().trim();
        String desc = descInput.getText().toString().trim();
        String state = stateInput.getText().toString().trim();
        String startDate = startDateInput.getText().toString().trim();
        String endDate = endDateInput.getText().toString().trim();

        if (title.isEmpty()) { titleInput.setError("Required"); return; }
        if (state.isEmpty()) { stateInput.setError("Required"); return; }
        if (startDate.isEmpty()) { startDateInput.setError("Required (yyyy-MM-dd)"); return; }

        EventRequest request = new EventRequest();
        request.setTitle(title);
        request.setDescription(desc);
        request.setState(state);
        request.setLatitude(25.5);
        request.setLongitude(91.8);
        request.setLocationSource("MANUAL");
        request.setEventType(eventTypes[typeSpinner.getSelectedItemPosition()]);
        request.setStartDate(startDate);
        request.setEndDate(endDate.isEmpty() ? startDate : endDate);

        submitBtn.setEnabled(false);

        ApiService api = ApiClient.getApiService(this);
        api.createEvent(request).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                submitBtn.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(AddEventActivity.this, "Event created! 🎉", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddEventActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                submitBtn.setEnabled(true);
                Toast.makeText(AddEventActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
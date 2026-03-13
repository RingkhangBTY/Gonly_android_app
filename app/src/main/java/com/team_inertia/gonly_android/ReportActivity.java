package com.team_inertia.gonly_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.api.ApiService;
import com.team_inertia.gonly_android.model.ApiResponse;
import com.team_inertia.gonly_android.model.ReportRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    private EditText reportDescription;
    private Spinner resonSpinner;
    private Button submitReportBtn;

    private final String [] reasons = {"SPAM","FAKE","OFFENSIVE","DUPLICATE","INCORRECT_LOCATION","OTHER"};
    private ArrayAdapter<String> adapter;
    private ReportRequest reportRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reportDescription = findViewById(R.id.reportDescription);
        resonSpinner = findViewById(R.id.reportReasonSpinner);
        submitReportBtn = findViewById(R.id.reportSubmitBtn);

        adapter = new ArrayAdapter<>(
                this,android.R.layout.simple_spinner_dropdown_item,reasons
        );
        resonSpinner.setAdapter(adapter);

        submitReportBtn.setOnClickListener( view -> {
            Intent intent = getIntent();
            Long gemId = intent.getLongExtra("gemId",00);
            if (reportDescription.getText().toString().isEmpty()){
                Toast.makeText(this,"Plz enter report description",Toast.LENGTH_SHORT).show();
                return;
            }
            reportRequest = new ReportRequest(gemId,
                    resonSpinner.getSelectedItem().toString(),
                    reportDescription.getText().toString());

            ApiService apiService = ApiClient.getApiService(this);
            apiService.createReport(reportRequest).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body().isSuccess()){
                        Toast.makeText(ReportActivity.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ReportActivity.this,"Fails to submit report!..",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(ReportActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
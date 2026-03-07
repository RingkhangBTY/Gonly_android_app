package com.team_inertia.gonly_android;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.api.ApiService;
import com.team_inertia.gonly_android.model.ApiResponse;
import com.team_inertia.gonly_android.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, fullNameInput, homeStateInput;
    private Button registerButton;
    private TextView goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.regEmail);
        passwordInput = findViewById(R.id.regPassword);
        fullNameInput = findViewById(R.id.regFullName);
        homeStateInput = findViewById(R.id.regHomeState);
        registerButton = findViewById(R.id.registerButton);
        goToLogin = findViewById(R.id.goToLogin);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String fullName = fullNameInput.getText().toString().trim();
                String homeState = homeStateInput.getText().toString().trim();

                if (email.isEmpty()) { emailInput.setError("Required"); return; }
                if (password.isEmpty()) { passwordInput.setError("Required"); return; }
                if (fullName.isEmpty()) { fullNameInput.setError("Required"); return; }

                doRegister(email, password, fullName, homeState);
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // go back to login
            }
        });
    }

    private void doRegister(String email, String password, String fullName, String homeState) {
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");

        ApiService api = ApiClient.getApiService(this);
        RegisterRequest request = new RegisterRequest(email, password, fullName, homeState, "");

        api.register(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                registerButton.setEnabled(true);
                registerButton.setText("Register");

                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registered! Please login.", Toast.LENGTH_SHORT).show();
                    finish(); // go back to login
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                registerButton.setEnabled(true);
                registerButton.setText("Register");
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
package com.team_inertia.gonly_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.team_inertia.gonly_android.api.ApiClient;
import com.team_inertia.gonly_android.api.ApiService;
import com.team_inertia.gonly_android.model.LoginRequest;
import com.team_inertia.gonly_android.model.LoginResponse;
import com.team_inertia.gonly_android.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView goToRegister;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        goToRegister = findViewById(R.id.goToRegister);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (email.isEmpty()) { emailInput.setError("Enter email"); return; }
                if (password.isEmpty()) { passwordInput.setError("Enter password"); return; }

                doLogin(email, password);
            }
        });

        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void doLogin(String email, String password) {
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Reset retrofit so it picks up the new token
        ApiClient.resetClient();
        ApiService api = ApiClient.getApiService(this);
        LoginRequest request = new LoginRequest(email, password);

        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loginButton.setEnabled(true);
                loginButton.setText("Login");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginData = response.body();

                    sessionManager.saveLogin(
                            loginData.getToken(),
                            loginData.getEmail(),
                            loginData.getFullName(),
                            loginData.getUserId()
                    );

                    // Reset client again so new requests use the token
                    ApiClient.resetClient();

                    Toast.makeText(LoginActivity.this, "Welcome " + loginData.getFullName(), Toast.LENGTH_SHORT).show();
                    finish(); // just go back to wherever the user came from
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
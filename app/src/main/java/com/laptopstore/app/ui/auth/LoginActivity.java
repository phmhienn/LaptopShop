package com.laptopstore.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.auth.JwtResponse;
import com.laptopstore.app.data.model.auth.LoginRequest;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.data.network.TokenManager;
import com.laptopstore.app.databinding.ActivityLoginBinding;
import com.laptopstore.app.ui.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = new TokenManager(this);

        // Auto-login if token exists
        if (tokenManager.isLoggedIn()) {
            startMainActivity();
        }

        binding.btnLogin.setOnClickListener(v -> performLogin());
        
        binding.tvRegisterPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnLogin.setEnabled(false);

        LoginRequest request = new LoginRequest(username, password);
        
        ApiClient.getApiService(this).login(request).enqueue(new Callback<ApiResponse<JwtResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<JwtResponse>> call, Response<ApiResponse<JwtResponse>> response) {
                binding.btnLogin.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    JwtResponse jwtResponse = response.body().getData();
                    
                    tokenManager.saveToken(jwtResponse.getToken(), jwtResponse.getRefreshToken());
                    tokenManager.saveUserInfo(jwtResponse.getId(), jwtResponse.getFullName(), jwtResponse.getRoles());
                    
                    startMainActivity();
                } else {
                    String errorMessage = "Login Failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorStr = response.errorBody().string();
                            org.json.JSONObject jsonObject = new org.json.JSONObject(errorStr);
                            if (jsonObject.has("message")) {
                                errorMessage = jsonObject.getString("message");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<JwtResponse>> call, Throwable t) {
                binding.btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

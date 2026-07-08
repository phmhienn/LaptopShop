package com.laptopstore.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.auth.SignupRequest;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.ActivityRegisterBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(v -> performRegister());
        
        binding.tvLoginPrompt.setOnClickListener(v -> {
            finish(); // Go back to login
        });
    }

    private void performRegister() {
        String username = binding.etUsername.getText() != null ? binding.etUsername.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String fullName = binding.etFullName.getText() != null ? binding.etFullName.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnRegister.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        SignupRequest request = new SignupRequest(username, email, password, fullName);
        
        ApiClient.getApiService(this).signup(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                binding.btnRegister.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to login
                } else {
                    String message = "Registration Failed";
                    try {
                        if (response.errorBody() != null) {
                            String errorStr = response.errorBody().string();
                            org.json.JSONObject jsonObject = new org.json.JSONObject(errorStr);
                            if (jsonObject.has("message")) {
                                message = jsonObject.getString("message");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                binding.btnRegister.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

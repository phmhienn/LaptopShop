package com.laptopstore.app.ui.admin.user;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.laptopstore.app.R;
import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.user.User;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentAdminUserDetailBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserDetailFragment extends Fragment {

    private FragmentAdminUserDetailBinding binding;
    private Long userId = -1L;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminUserDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getLong("userId", -1L);
        }

        if (userId != -1L) {
            loadUserDetails();
        } else {
            showToast("Invalid User ID");
            if (getActivity() != null) getActivity().onBackPressed();
        }

        binding.btnToggleStatus.setOnClickListener(v -> toggleStatus());
    }

    private void loadUserDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).getUserById(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    populateUser(user);
                } else {
                    showToast("Failed to load user");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                if (binding != null) binding.progressBar.setVisibility(View.GONE);
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void populateUser(User user) {
        binding.tvUsername.setText("Username: " + user.getUsername());
        binding.tvFullName.setText("Full Name: " + (user.getFullName() != null ? user.getFullName() : ""));
        binding.tvEmail.setText("Email: " + user.getEmail());
        binding.tvPhone.setText("Phone: " + (user.getPhone() != null ? user.getPhone() : ""));
        
        String rolesStr = user.getRoles() != null ? TextUtils.join(", ", user.getRoles()) : "USER";
        binding.tvRoles.setText("Roles: " + rolesStr);
        
        binding.tvCreatedAt.setText("Created At: " + (user.getCreatedAt() != null ? user.getCreatedAt() : ""));
        
        if (user.isEnabled()) {
            binding.tvStatus.setText("Status: ACTIVE");
            binding.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else {
            binding.tvStatus.setText("Status: BLOCKED");
            binding.tvStatus.setTextColor(Color.parseColor("#F44336")); // Red
        }

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(this)
                 .load(user.getAvatar())
                 .placeholder(R.mipmap.ic_launcher)
                 .circleCrop()
                 .into(binding.ivAvatar);
        } else {
            binding.ivAvatar.setImageResource(R.mipmap.ic_launcher);
        }
    }

    private void toggleStatus() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService(requireContext()).toggleUserStatus(userId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    showToast("User status toggled");
                    loadUserDetails(); // Reload to reflect changes
                } else {
                    showToast("Failed to toggle status");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                if (binding != null) binding.progressBar.setVisibility(View.GONE);
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void showToast(String msg) {
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

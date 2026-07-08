package com.laptopstore.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.user.User;
import com.laptopstore.app.data.model.user.UserProfileUpdateDTO;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentEditProfileBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadProfile();

        binding.btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentUser = response.body().getData();
                    displayUser(currentUser);
                } else {
                    Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayUser(User user) {
        if (user == null) return;

        binding.etFullName.setText(user.getFullName() != null ? user.getFullName() : "");
        binding.etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
        binding.etAddress.setText(user.getAddress() != null ? user.getAddress() : "");
    }

    private void saveProfile() {
        String fullName = binding.etFullName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String currentPassword = binding.etCurrentPassword.getText().toString().trim();
        String newPassword = binding.etNewPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            binding.etFullName.setError("Full Name is required");
            return;
        }

        UserProfileUpdateDTO request = new UserProfileUpdateDTO(
                fullName,
                phone,
                address,
                currentPassword.isEmpty() ? null : currentPassword,
                newPassword.isEmpty() ? null : newPassword
        );

        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).updateProfile(request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

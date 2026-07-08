package com.laptopstore.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
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
import com.laptopstore.app.databinding.FragmentProfileBinding;
import com.laptopstore.app.ui.auth.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadProfile();

        com.laptopstore.app.data.network.TokenManager tokenManager = new com.laptopstore.app.data.network.TokenManager(requireContext());
        if (tokenManager.isAdmin()) {
            binding.btnAdminPanel.setVisibility(View.VISIBLE);
        }

        binding.btnLogout.setOnClickListener(v -> performLogout());
        
        binding.btnAdminPanel.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.action_navigation_profile_to_adminDashboardFragment);
        });

        binding.btnEditProfile.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.action_navigation_profile_to_editProfileFragment);
        });

        binding.btnMyOrders.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.action_navigation_profile_to_orderHistoryFragment);
        });

        binding.btnMyWishlist.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.action_navigation_profile_to_wishlistFragment);
        });
    }

    private void loadProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    displayUser(response.body().getData());
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

        binding.tvFullName.setText(user.getFullName() != null ? user.getFullName() : "N/A");
        binding.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
        binding.tvPhone.setText(user.getPhone() != null ? user.getPhone() : "N/A");
        binding.tvAddress.setText(user.getAddress() != null ? user.getAddress() : "N/A");

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(this)
                 .load(user.getAvatar())
                 .placeholder(R.mipmap.ic_launcher_round)
                 .into(binding.ivAvatar);
        } else {
            binding.ivAvatar.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    private void performLogout() {
        if (getContext() != null) {
            com.laptopstore.app.data.network.TokenManager tokenManager = new com.laptopstore.app.data.network.TokenManager(getContext());
            tokenManager.clear();
        }

        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

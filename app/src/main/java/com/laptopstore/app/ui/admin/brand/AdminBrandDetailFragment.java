package com.laptopstore.app.ui.admin.brand;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.product.Brand;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentAdminBrandDetailBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminBrandDetailFragment extends Fragment {

    private FragmentAdminBrandDetailBinding binding;
    private Long brandId = -1L;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminBrandDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            brandId = getArguments().getLong("brandId", -1L);
            isEditMode = brandId != -1L;
        }

        if (isEditMode) {
            binding.tvTitle.setText("Edit Brand");
            binding.btnToggleStatus.setVisibility(View.VISIBLE);
            loadBrandDetails();
        } else {
            binding.tvTitle.setText("Add New Brand");
            binding.btnToggleStatus.setVisibility(View.GONE);
        }

        binding.btnSave.setOnClickListener(v -> saveBrand());
        binding.btnToggleStatus.setOnClickListener(v -> toggleStatus());
    }

    private void loadBrandDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).getBrandById(brandId).enqueue(new Callback<ApiResponse<Brand>>() {
            @Override
            public void onResponse(Call<ApiResponse<Brand>> call, Response<ApiResponse<Brand>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Brand brand = response.body().getData();
                    binding.etName.setText(brand.getName());
                    binding.etDescription.setText(brand.getDescription());
                    binding.switchActive.setChecked(brand.isActive());
                } else {
                    showToast("Failed to load brand");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Brand>> call, Throwable t) {
                if (binding != null) binding.progressBar.setVisibility(View.GONE);
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void saveBrand() {
        String name = binding.etName.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        boolean isActive = binding.switchActive.isChecked();

        if (name.isEmpty()) {
            binding.etName.setError("Name is required");
            return;
        }

        Brand brand = new Brand();
        brand.setName(name);
        brand.setDescription(description);
        brand.setActive(isActive);

        binding.progressBar.setVisibility(View.VISIBLE);

        if (isEditMode) {
            ApiClient.getApiService(requireContext()).updateBrand(brandId, brand).enqueue(saveCallback);
        } else {
            ApiClient.getApiService(requireContext()).createBrand(brand).enqueue(saveCallback);
        }
    }

    private final Callback<ApiResponse<Brand>> saveCallback = new Callback<ApiResponse<Brand>>() {
        @Override
        public void onResponse(Call<ApiResponse<Brand>> call, Response<ApiResponse<Brand>> response) {
            binding.progressBar.setVisibility(View.GONE);
            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                showToast("Brand saved successfully");
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            } else {
                showToast("Failed to save brand");
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<Brand>> call, Throwable t) {
            if (binding != null) binding.progressBar.setVisibility(View.GONE);
            showToast("Error: " + t.getMessage());
        }
    };

    private void toggleStatus() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService(requireContext()).toggleBrandStatus(brandId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    showToast("Status toggled");
                    loadBrandDetails(); // Reload to reflect changes
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

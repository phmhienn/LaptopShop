package com.laptopstore.app.ui.admin.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.product.Category;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentAdminCategoryDetailBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoryDetailFragment extends Fragment {

    private FragmentAdminCategoryDetailBinding binding;
    private Long categoryId = -1L;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminCategoryDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            categoryId = getArguments().getLong("categoryId", -1L);
            isEditMode = categoryId != -1L;
        }

        if (isEditMode) {
            binding.tvTitle.setText("Edit Category");
            binding.btnToggleStatus.setVisibility(View.VISIBLE);
            loadCategoryDetails();
        } else {
            binding.tvTitle.setText("Add New Category");
            binding.btnToggleStatus.setVisibility(View.GONE);
        }

        binding.btnSave.setOnClickListener(v -> saveCategory());
        binding.btnToggleStatus.setOnClickListener(v -> toggleStatus());
    }

    private void loadCategoryDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).getCategoryById(categoryId).enqueue(new Callback<ApiResponse<Category>>() {
            @Override
            public void onResponse(Call<ApiResponse<Category>> call, Response<ApiResponse<Category>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Category category = response.body().getData();
                    binding.etName.setText(category.getName());
                    binding.etDescription.setText(category.getDescription());
                    binding.switchActive.setChecked(category.isActive());
                } else {
                    showToast("Failed to load category");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {
                if (binding != null) binding.progressBar.setVisibility(View.GONE);
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void saveCategory() {
        String name = binding.etName.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        boolean isActive = binding.switchActive.isChecked();

        if (name.isEmpty()) {
            binding.etName.setError("Name is required");
            return;
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setActive(isActive);

        binding.progressBar.setVisibility(View.VISIBLE);

        if (isEditMode) {
            ApiClient.getApiService(requireContext()).updateCategory(categoryId, category).enqueue(saveCallback);
        } else {
            ApiClient.getApiService(requireContext()).createCategory(category).enqueue(saveCallback);
        }
    }

    private final Callback<ApiResponse<Category>> saveCallback = new Callback<ApiResponse<Category>>() {
        @Override
        public void onResponse(Call<ApiResponse<Category>> call, Response<ApiResponse<Category>> response) {
            binding.progressBar.setVisibility(View.GONE);
            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                showToast("Category saved successfully");
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            } else {
                showToast("Failed to save category");
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {
            if (binding != null) binding.progressBar.setVisibility(View.GONE);
            showToast("Error: " + t.getMessage());
        }
    };

    private void toggleStatus() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService(requireContext()).toggleCategoryStatus(categoryId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    showToast("Status toggled");
                    loadCategoryDetails(); // Reload to reflect changes
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

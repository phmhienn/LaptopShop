package com.laptopstore.app.ui.admin.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.laptopstore.app.R;
import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.PagedResponse;
import com.laptopstore.app.data.model.product.Category;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentAdminCategoryListBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoryListFragment extends Fragment {

    private FragmentAdminCategoryListBinding binding;
    private AdminCategoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminCategoryListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        
        binding.fabAddCategory.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_adminCategoryListFragment_to_adminCategoryDetailFragment);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories(); // Reload when returning to this screen
    }

    private void setupRecyclerView() {
        adapter = new AdminCategoryAdapter(category -> {
            Bundle bundle = new Bundle();
            bundle.putLong("categoryId", category.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_adminCategoryListFragment_to_adminCategoryDetailFragment, bundle);
        });
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCategories.setAdapter(adapter);
    }

    private void loadCategories() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).getAllCategories(0, 50).enqueue(new Callback<ApiResponse<PagedResponse<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResponse<Category>>> call, Response<ApiResponse<PagedResponse<Category>>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setCategories(response.body().getData().getContent());
                } else {
                    showToast("Failed to load categories");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResponse<Category>>> call, Throwable t) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                showToast("Network Error: " + t.getMessage());
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

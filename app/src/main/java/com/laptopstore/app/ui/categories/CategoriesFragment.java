package com.laptopstore.app.ui.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.product.Category;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.R;
import com.laptopstore.app.databinding.FragmentCategoriesBinding;
import com.laptopstore.app.ui.adapter.CategoryAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends Fragment {

    private FragmentCategoriesBinding binding;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        loadCategories();
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter();
        // 2 columns grid
        binding.rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvCategories.setAdapter(categoryAdapter);

        categoryAdapter.setOnCategoryClickListener(category -> {
            Bundle bundle = new Bundle();
            bundle.putLong("categoryId", category.getId());
            bundle.putString("categoryName", category.getName());
            androidx.navigation.Navigation.findNavController(requireView()).navigate(R.id.action_navigation_categories_to_categoryProductsFragment, bundle);
        });
    }

    private void loadCategories() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).getCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                binding.progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    categoryAdapter.setCategories(response.body().getData());
                } else {
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

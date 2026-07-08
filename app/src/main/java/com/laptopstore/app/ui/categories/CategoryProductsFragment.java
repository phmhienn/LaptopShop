package com.laptopstore.app.ui.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.laptopstore.app.R;
import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.PagedResponse;
import com.laptopstore.app.data.model.product.Product;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentCategoryProductsBinding;
import com.laptopstore.app.ui.adapter.ProductAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryProductsFragment extends Fragment {

    private FragmentCategoryProductsBinding binding;
    private ProductAdapter productAdapter;
    private Long categoryId;
    private String categoryName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            categoryId = getArguments().getLong("categoryId", -1L);
            categoryName = getArguments().getString("categoryName", "Products");
        }

        binding.tvCategoryName.setText(categoryName);

        setupRecyclerView();
        
        if (categoryId != -1L) {
            loadProducts();
        } else {
            showToast("Invalid Category");
        }
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter();
        binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvProducts.setAdapter(productAdapter);

        productAdapter.setOnProductClickListener(product -> {
            Bundle bundle = new Bundle();
            bundle.putLong("productId", product.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_categoryProductsFragment_to_productDetailFragment, bundle);
        });
    }

    private void loadProducts() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvNoProducts.setVisibility(View.GONE);

        ApiClient.getApiService(requireContext()).getProductsByCategory(categoryId, 0, 50).enqueue(new Callback<ApiResponse<PagedResponse<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResponse<Product>>> call, Response<ApiResponse<PagedResponse<Product>>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PagedResponse<Product> pagedResponse = response.body().getData();
                    if (pagedResponse != null && pagedResponse.getContent() != null && !pagedResponse.getContent().isEmpty()) {
                        productAdapter.setProducts(pagedResponse.getContent());
                        binding.rvProducts.setVisibility(View.VISIBLE);
                    } else {
                        binding.rvProducts.setVisibility(View.GONE);
                        binding.tvNoProducts.setVisibility(View.VISIBLE);
                    }
                } else {
                    showToast("Failed to load products");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResponse<Product>>> call, Throwable t) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                showToast("Network Error: " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.laptopstore.app.ui.home;

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
import com.laptopstore.app.data.model.PagedResponse;
import com.laptopstore.app.data.model.product.Product;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentHomeBinding;
import com.laptopstore.app.ui.adapter.ProductAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        binding.cvSearchBar.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view)
                .navigate(com.laptopstore.app.R.id.action_navigation_home_to_searchFilterFragment);
        });

        setupRecyclerView();
        loadFeaturedProducts();
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter();
        binding.rvFeaturedProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvFeaturedProducts.setAdapter(productAdapter);
        
        productAdapter.setOnProductClickListener(product -> {
            Bundle bundle = new Bundle();
            bundle.putLong("productId", product.getId());
            androidx.navigation.Navigation.findNavController(getView())
                .navigate(com.laptopstore.app.R.id.action_navigation_home_to_productDetailFragment, bundle);
        });
    }

    private void loadFeaturedProducts() {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        ApiClient.getApiService(requireContext()).getFeaturedProducts(0, 20).enqueue(new Callback<ApiResponse<PagedResponse<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResponse<Product>>> call, Response<ApiResponse<PagedResponse<Product>>> response) {
                binding.progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    productAdapter.setProducts(response.body().getData().getContent());
                } else {
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResponse<Product>>> call, Throwable t) {
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

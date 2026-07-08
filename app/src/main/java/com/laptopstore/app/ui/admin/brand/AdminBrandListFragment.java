package com.laptopstore.app.ui.admin.brand;

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
import com.laptopstore.app.data.model.product.Brand;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentAdminBrandListBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminBrandListFragment extends Fragment {

    private FragmentAdminBrandListBinding binding;
    private AdminBrandAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminBrandListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        
        binding.fabAddBrand.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_adminBrandListFragment_to_adminBrandDetailFragment);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBrands(); // Reload when returning to this screen
    }

    private void setupRecyclerView() {
        adapter = new AdminBrandAdapter(brand -> {
            Bundle bundle = new Bundle();
            bundle.putLong("brandId", brand.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_adminBrandListFragment_to_adminBrandDetailFragment, bundle);
        });
        binding.rvBrands.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBrands.setAdapter(adapter);
    }

    private void loadBrands() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).getAllBrands(0, 50).enqueue(new Callback<ApiResponse<PagedResponse<Brand>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResponse<Brand>>> call, Response<ApiResponse<PagedResponse<Brand>>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setBrands(response.body().getData().getContent());
                } else {
                    showToast("Failed to load brands");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResponse<Brand>>> call, Throwable t) {
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

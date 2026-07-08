package com.laptopstore.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.laptopstore.app.R;
import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.admin.DashboardStats;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentAdminDashboardBinding;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadStats();

        binding.btnManageOrders.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.action_adminDashboardFragment_to_adminOrderListFragment);
        });
        binding.btnManageProducts.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.action_adminDashboardFragment_to_adminProductListFragment);
        });
        binding.btnManageCategories.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.action_adminDashboardFragment_to_adminCategoryListFragment);
        });
        binding.btnManageBrands.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.action_adminDashboardFragment_to_adminBrandListFragment);
        });
        binding.btnManageUsers.setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.action_adminDashboardFragment_to_adminUserListFragment);
        });
        binding.btnManageCoupons.setOnClickListener(v -> showToast("Manage Coupons coming soon"));
    }

    private void loadStats() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).getDashboardStats().enqueue(new Callback<ApiResponse<DashboardStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardStats>> call, Response<ApiResponse<DashboardStats>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    DashboardStats stats = response.body().getData();
                    if (stats != null) {
                        binding.tvTotalOrders.setText(String.valueOf(stats.getTotalOrders()));
                        
                        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        binding.tvTotalRevenue.setText(format.format(stats.getTotalRevenue() != null ? stats.getTotalRevenue() : 0));
                    }
                } else {
                    showToast("Failed to load dashboard stats");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardStats>> call, Throwable t) {
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

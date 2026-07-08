package com.laptopstore.app.ui.admin.order;

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
import com.laptopstore.app.data.model.order.Order;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentAdminOrderListBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderListFragment extends Fragment {

    private FragmentAdminOrderListBinding binding;
    private AdminOrderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminOrderListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        loadOrders();
    }

    private void setupRecyclerView() {
        adapter = new AdminOrderAdapter(order -> {
            Bundle bundle = new Bundle();
            bundle.putLong("orderId", order.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_adminOrderListFragment_to_adminOrderDetailFragment, bundle);
        });
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvOrders.setAdapter(adapter);
    }

    private void loadOrders() {
        binding.progressBar.setVisibility(View.VISIBLE);

        // Fetch first page, size 20
        ApiClient.getApiService(requireContext()).searchOrders(0, 20).enqueue(new Callback<ApiResponse<PagedResponse<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResponse<Order>>> call, Response<ApiResponse<PagedResponse<Order>>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setOrders(response.body().getData().getContent());
                } else {
                    showToast("Failed to load orders");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResponse<Order>>> call, Throwable t) {
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

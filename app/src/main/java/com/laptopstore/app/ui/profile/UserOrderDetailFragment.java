package com.laptopstore.app.ui.profile;

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
import com.laptopstore.app.data.model.order.Order;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentUserOrderDetailBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserOrderDetailFragment extends Fragment {

    private FragmentUserOrderDetailBinding binding;
    private UserOrderDetailAdapter adapter;
    private Long orderId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserOrderDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            orderId = getArguments().getLong("orderId", -1);
        }

        if (orderId == -1) {
            Toast.makeText(getContext(), "Invalid order ID", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
            return;
        }

        binding.rvOrderItems.setLayoutManager(new LinearLayoutManager(getContext()));

        loadOrderDetails();
    }

    private void loadOrderDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService(requireContext()).getMyOrderDetails(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Order order = response.body().getData();
                    displayOrderDetails(order);
                } else {
                    Toast.makeText(getContext(), "Failed to load order details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOrderDetails(Order order) {
        binding.tvOrderCode.setText(order.getOrderCode());
        binding.tvOrderStatus.setText(order.getStatus());

        boolean isDelivered = "DELIVERED".equalsIgnoreCase(order.getStatus());
        adapter = new UserOrderDetailAdapter(isDelivered, item -> {
            Bundle bundle = new Bundle();
            bundle.putLong("productId", item.getProductId());
            bundle.putString("productName", item.getProductName());
            Navigation.findNavController(requireView()).navigate(R.id.action_userOrderDetailFragment_to_writeReviewFragment, bundle);
        });

        binding.rvOrderItems.setAdapter(adapter);

        if (order.getItems() != null) {
            adapter.setItems(order.getItems());
        }
    }
}

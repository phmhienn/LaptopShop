package com.laptopstore.app.ui.admin.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.laptopstore.app.R;
import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.order.Order;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentAdminOrderDetailBinding;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderDetailFragment extends Fragment {

    private FragmentAdminOrderDetailBinding binding;
    private Long orderId = -1L;
    private Order currentOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminOrderDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            orderId = getArguments().getLong("orderId", -1L);
        }

        if (orderId != -1L) {
            loadOrderDetails();
        }

        binding.btnUpdateStatus.setOnClickListener(v -> updateOrderStatus());
    }

    private void loadOrderDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).getOrderById(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentOrder = response.body().getData();
                    displayOrderDetails(currentOrder);
                } else {
                    showToast("Failed to load order details");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                showToast("Network Error: " + t.getMessage());
            }
        });
    }

    private void displayOrderDetails(Order order) {
        binding.tvOrderCode.setText("Code: " + (order.getOrderCode() != null ? order.getOrderCode() : "#" + order.getId()));
        binding.tvShippingName.setText("Customer: " + order.getShippingName() + " - " + order.getShippingPhone());
        binding.tvShippingAddress.setText("Address: " + order.getShippingAddress());

        // Set spinner selection based on current status
        String[] statuses = getResources().getStringArray(R.array.order_status_array);
        int position = Arrays.asList(statuses).indexOf(order.getStatus());
        if (position >= 0) {
            binding.spinnerStatus.setSelection(position);
        }
    }

    private void updateOrderStatus() {
        if (currentOrder == null) return;
        
        String newStatus = binding.spinnerStatus.getSelectedItem().toString();

        if (currentOrder.getShipment() == null) {
            showToast("No shipment found for this order");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        Long shipmentId = currentOrder.getShipment().getId();
        ApiClient.getApiService(requireContext()).updateShipmentStatus(shipmentId, newStatus).enqueue(new Callback<ApiResponse<com.laptopstore.app.data.model.order.Shipment>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.laptopstore.app.data.model.order.Shipment>> call, Response<ApiResponse<com.laptopstore.app.data.model.order.Shipment>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    showToast("Order status updated to " + newStatus);
                    // Just refresh the order by fetching it again or manually update the shipment
                    loadOrderDetails();
                } else {
                    showToast("Failed to update order status");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.laptopstore.app.data.model.order.Shipment>> call, Throwable t) {
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

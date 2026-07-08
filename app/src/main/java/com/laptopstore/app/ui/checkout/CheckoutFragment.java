package com.laptopstore.app.ui.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.laptopstore.app.R;
import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.order.CheckoutRequest;
import com.laptopstore.app.data.model.order.Order;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentCheckoutBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserProfile();
        binding.btnPlaceOrder.setOnClickListener(v -> attemptCheckout());
    }

    private void loadUserProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService(requireContext()).getProfile().enqueue(new Callback<ApiResponse<com.laptopstore.app.data.model.user.User>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.laptopstore.app.data.model.user.User>> call, Response<ApiResponse<com.laptopstore.app.data.model.user.User>> response) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    com.laptopstore.app.data.model.user.User user = response.body().getData();
                    if (user != null) {
                        if (user.getFullName() != null) binding.etName.setText(user.getFullName());
                        if (user.getPhone() != null) binding.etPhone.setText(user.getPhone());
                        if (user.getAddress() != null) binding.etAddress.setText(user.getAddress());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.laptopstore.app.data.model.user.User>> call, Throwable t) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void attemptCheckout() {
        String name = binding.etName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String note = binding.etNote.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etName.setError("Name is required");
            binding.etName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            binding.etPhone.setError("Phone is required");
            binding.etPhone.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            binding.etAddress.setError("Address is required");
            binding.etAddress.requestFocus();
            return;
        }

        String paymentMethod = "COD";
        if (binding.rbCreditCard.isChecked()) {
            paymentMethod = "CREDIT_CARD";
        }

        CheckoutRequest request = new CheckoutRequest();
        request.setShippingName(name);
        request.setShippingPhone(phone);
        request.setShippingAddress(address);
        request.setNote(note);
        request.setPaymentMethod(paymentMethod);

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnPlaceOrder.setEnabled(false);

        ApiClient.getApiService(requireContext()).checkout(request).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnPlaceOrder.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    showToast("Order placed successfully!");
                    // Navigate back to home or success screen
                    Navigation.findNavController(requireView()).navigate(R.id.action_checkoutFragment_to_navigation_home);
                } else {
                    String msg = "Failed to place order";
                    if (response.body() != null && response.body().getMessage() != null) {
                        msg = response.body().getMessage();
                    }
                    showToast(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnPlaceOrder.setEnabled(true);
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

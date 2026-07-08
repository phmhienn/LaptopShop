package com.laptopstore.app.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.order.Cart;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentCartBinding;
import com.laptopstore.app.ui.adapter.CartAdapter;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private CartAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupRecyclerView();
        loadCart();

        binding.btnCheckout.setOnClickListener(v -> {
            if (cartAdapter.getItemCount() == 0) {
                Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            } else {
                androidx.navigation.Navigation.findNavController(view).navigate(com.laptopstore.app.R.id.action_navigation_cart_to_checkoutFragment);
            }
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter();
        binding.rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCart.setAdapter(cartAdapter);
    }

    private void loadCart() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).getCart().enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Cart cart = response.body().getData();
                    if (cart != null) {
                        cartAdapter.setCartItems(cart.getItems());
                        
                        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        binding.tvCartTotal.setText(format.format(cart.getTotalAmount() != null ? cart.getTotalAmount() : 0));
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load cart. Are you logged in?", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
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

package com.laptopstore.app.ui.wishlist;

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
import com.laptopstore.app.data.model.product.Product;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentWishlistBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistFragment extends Fragment implements WishlistAdapter.OnWishlistInteractionListener {

    private FragmentWishlistBinding binding;
    private WishlistAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new WishlistAdapter(this);
        binding.rvWishlist.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvWishlist.setAdapter(adapter);

        loadWishlist();
    }

    private void loadWishlist() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);
        binding.rvWishlist.setVisibility(View.GONE);

        ApiClient.getApiService(requireContext()).getWishlist().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Product> products = response.body().getData();
                    if (products == null || products.isEmpty()) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        binding.rvWishlist.setVisibility(View.VISIBLE);
                        adapter.setProducts(products);
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                }
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onProductClick(Product product) {
        Bundle bundle = new Bundle();
        bundle.putLong("productId", product.getId());
        Navigation.findNavController(requireView()).navigate(R.id.action_wishlistFragment_to_productDetailFragment, bundle);
    }

    @Override
    public void onRemoveClick(Product product, int position) {
        ApiClient.getApiService(requireContext()).removeFromWishlist(product.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    adapter.removeProduct(position);
                    Toast.makeText(getContext(), "Đã xoá khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    
                    if (adapter.getItemCount() == 0) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        binding.rvWishlist.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi xoá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

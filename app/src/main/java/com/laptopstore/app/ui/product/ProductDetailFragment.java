package com.laptopstore.app.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.laptopstore.app.R;
import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.product.ProductDetail;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentProductDetailBinding;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.recyclerview.widget.LinearLayoutManager;
import com.laptopstore.app.data.model.PagedResponse;
import com.laptopstore.app.data.model.product.Review;

public class ProductDetailFragment extends Fragment {

    private FragmentProductDetailBinding binding;
    private Long productId;
    private ProductReviewAdapter reviewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        reviewAdapter = new ProductReviewAdapter();
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvReviews.setAdapter(reviewAdapter);

        if (getArguments() != null) {
            productId = getArguments().getLong("productId", -1);
        }

        if (productId != null && productId != -1) {
            loadProductDetail(productId);
            loadProductReviews(productId);
            checkWishlistState(productId);
        } else {
            Toast.makeText(getContext(), "Invalid product ID", Toast.LENGTH_SHORT).show();
        }

        binding.btnAddToCart.setOnClickListener(v -> {
            if (productId != null && productId != -1) {
                addToCart(productId);
            }
        });

        binding.ivWishlist.setOnClickListener(v -> {
            if (productId != null && productId != -1) {
                toggleWishlist(productId);
            }
        });
    }

    private boolean isInWishlist = false;

    private void checkWishlistState(Long id) {
        ApiClient.getApiService(requireContext()).checkProductInWishlist(id).enqueue(new Callback<ApiResponse<Boolean>>() {
            @Override
            public void onResponse(Call<ApiResponse<Boolean>> call, Response<ApiResponse<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    isInWishlist = response.body().getData() != null && response.body().getData();
                    updateWishlistIcon();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {
                // Ignore failure
            }
        });
    }

    private void updateWishlistIcon() {
        if (isInWishlist) {
            binding.ivWishlist.setImageResource(R.drawable.ic_heart_filled);
        } else {
            binding.ivWishlist.setImageResource(R.drawable.ic_heart_outline);
        }
    }

    private void toggleWishlist(Long id) {
        binding.ivWishlist.setEnabled(false);
        if (isInWishlist) {
            ApiClient.getApiService(requireContext()).removeFromWishlist(id).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    binding.ivWishlist.setEnabled(true);
                    if (response.isSuccessful()) {
                        isInWishlist = false;
                        updateWishlistIcon();
                        Toast.makeText(getContext(), "Đã xoá khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    binding.ivWishlist.setEnabled(true);
                }
            });
        } else {
            ApiClient.getApiService(requireContext()).addToWishlist(id).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    binding.ivWishlist.setEnabled(true);
                    if (response.isSuccessful()) {
                        isInWishlist = true;
                        updateWishlistIcon();
                        Toast.makeText(getContext(), "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    binding.ivWishlist.setEnabled(true);
                }
            });
        }
    }

    private void loadProductReviews(Long id) {
        ApiClient.getApiService(requireContext()).getProductReviews(id, 0, 50).enqueue(new Callback<ApiResponse<PagedResponse<Review>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResponse<Review>>> call, Response<ApiResponse<PagedResponse<Review>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PagedResponse<Review> pagedResponse = response.body().getData();
                    if (pagedResponse != null && pagedResponse.getContent() != null && !pagedResponse.getContent().isEmpty()) {
                        reviewAdapter.setReviews(pagedResponse.getContent());
                        binding.tvNoReviews.setVisibility(View.GONE);
                    } else {
                        binding.tvNoReviews.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.tvNoReviews.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResponse<Review>>> call, Throwable t) {
                if (binding != null) {
                    binding.tvNoReviews.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addToCart(Long id) {
        binding.btnAddToCart.setEnabled(false);
        binding.btnAddToCart.setText("Adding...");
        
        com.laptopstore.app.data.model.order.CartItemRequest request = new com.laptopstore.app.data.model.order.CartItemRequest(id, 1);
        
        ApiClient.getApiService(requireContext()).addItemToCart(request).enqueue(new Callback<ApiResponse<com.laptopstore.app.data.model.order.Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.laptopstore.app.data.model.order.Cart>> call, Response<ApiResponse<com.laptopstore.app.data.model.order.Cart>> response) {
                binding.btnAddToCart.setEnabled(true);
                binding.btnAddToCart.setText("Add to Cart");
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Added to cart successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to add to cart. Please login first.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.laptopstore.app.data.model.order.Cart>> call, Throwable t) {
                if (binding != null) {
                    binding.btnAddToCart.setEnabled(true);
                    binding.btnAddToCart.setText("Add to Cart");
                }
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadProductDetail(Long id) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnAddToCart.setEnabled(false);

        ApiClient.getApiService(requireContext()).getProductById(id).enqueue(new Callback<ApiResponse<ProductDetail>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductDetail>> call, Response<ApiResponse<ProductDetail>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    displayProduct(response.body().getData());
                    binding.btnAddToCart.setEnabled(true);
                } else {
                    Toast.makeText(getContext(), "Failed to load product details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductDetail>> call, Throwable t) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayProduct(ProductDetail product) {
        binding.tvProductName.setText(product.getName());
        
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceText = format.format(product.getEffectivePrice());
        binding.tvProductPrice.setText(priceText);
        
        if (product.getBrand() != null) {
            binding.tvProductBrand.setText("Brand: " + product.getBrand().getName());
            binding.tvProductBrand.setVisibility(View.VISIBLE);
        } else {
            binding.tvProductBrand.setVisibility(View.GONE);
        }
        
        binding.tvProductDescription.setText(product.getDescription());

        // Build specs text
        StringBuilder specs = new StringBuilder();
        if (product.getCpu() != null) specs.append("CPU: ").append(product.getCpu()).append("\n");
        if (product.getRam() != null) specs.append("RAM: ").append(product.getRam()).append("\n");
        if (product.getSsd() != null) specs.append("SSD: ").append(product.getSsd()).append("\n");
        if (product.getGpu() != null) specs.append("GPU: ").append(product.getGpu()).append("\n");
        if (product.getDisplay() != null) specs.append("Display: ").append(product.getDisplay()).append("\n");
        
        binding.tvProductSpecs.setText(specs.toString().trim());

        // Load thumbnail using Glide (assuming Glide is in dependencies)
        String imageUrl = product.getThumbnail();
        if (imageUrl == null || imageUrl.isEmpty()) {
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                imageUrl = product.getImages().get(0).getImageUrl();
            }
        }
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Need a full URL if backend only returns relative path, assuming backend returns full or we need to prepend base URL.
            // For now, let Glide handle it if it's full URL.
            Glide.with(this)
                 .load(imageUrl)
                 .placeholder(R.mipmap.ic_launcher)
                 .into(binding.ivProductImage);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

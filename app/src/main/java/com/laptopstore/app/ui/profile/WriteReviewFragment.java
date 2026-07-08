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

import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.product.Review;
import com.laptopstore.app.data.model.product.ReviewCreateRequest;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentWriteReviewBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteReviewFragment extends Fragment {

    private FragmentWriteReviewBinding binding;
    private Long productId;
    private String productName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWriteReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            productId = getArguments().getLong("productId", -1);
            productName = getArguments().getString("productName", "Product");
        }

        if (productId == -1) {
            Toast.makeText(getContext(), "Invalid product", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
            return;
        }

        binding.tvProductName.setText(productName);

        binding.btnSubmit.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        int rating = (int) binding.ratingBar.getRating();
        String comment = binding.etComment.getText().toString().trim();

        if (comment.isEmpty()) {
            Toast.makeText(getContext(), "Please write a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSubmit.setEnabled(false);

        ReviewCreateRequest request = new ReviewCreateRequest(productId, rating, comment);
        
        ApiClient.getApiService(requireContext()).createReview(request).enqueue(new Callback<ApiResponse<Review>>() {
            @Override
            public void onResponse(Call<ApiResponse<Review>> call, Response<ApiResponse<Review>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSubmit.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    String errorMsg = "Failed to submit review";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Review>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSubmit.setEnabled(true);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

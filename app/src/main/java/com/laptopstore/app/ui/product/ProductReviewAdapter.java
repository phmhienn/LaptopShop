package com.laptopstore.app.ui.product;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laptopstore.app.data.model.product.Review;
import com.laptopstore.app.databinding.ItemProductReviewBinding;

import java.util.ArrayList;
import java.util.List;

public class ProductReviewAdapter extends RecyclerView.Adapter<ProductReviewAdapter.ReviewViewHolder> {

    private List<Review> reviews = new ArrayList<>();

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductReviewBinding binding = ItemProductReviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductReviewBinding binding;

        public ReviewViewHolder(ItemProductReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Review review) {
            binding.tvUsername.setText(review.getUsername() != null ? review.getUsername() : "Anonymous");
            binding.ratingBar.setRating(review.getRating() != null ? review.getRating() : 0);
            binding.tvComment.setText(review.getComment() != null ? review.getComment() : "");
            
            if (review.getCreatedAt() != null) {
                // Keep it simple for now or format if needed
                binding.tvDate.setText(review.getCreatedAt().substring(0, Math.min(10, review.getCreatedAt().length())));
            } else {
                binding.tvDate.setText("");
            }
        }
    }
}

package com.laptopstore.app.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.laptopstore.app.data.model.order.OrderItem;
import com.laptopstore.app.databinding.ItemUserOrderDetailBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserOrderDetailAdapter extends RecyclerView.Adapter<UserOrderDetailAdapter.OrderItemViewHolder> {

    private List<OrderItem> items = new ArrayList<>();
    private final boolean isDelivered;
    private final OnReviewClickListener reviewClickListener;

    public interface OnReviewClickListener {
        void onReviewClick(OrderItem item);
    }

    public UserOrderDetailAdapter(boolean isDelivered, OnReviewClickListener reviewClickListener) {
        this.isDelivered = isDelivered;
        this.reviewClickListener = reviewClickListener;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserOrderDetailBinding binding = ItemUserOrderDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserOrderDetailBinding binding;

        public OrderItemViewHolder(@NonNull ItemUserOrderDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderItem item) {
            binding.tvProductName.setText(item.getProductName());
            binding.tvQuantity.setText("Qty: " + item.getQuantity());

            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.tvPrice.setText(format.format(item.getProductPrice()));

            if (item.getProductThumbnail() != null && !item.getProductThumbnail().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(item.getProductThumbnail())
                        .into(binding.ivProductImage);
            }

            if (isDelivered) {
                binding.btnReview.setVisibility(View.VISIBLE);
                binding.btnReview.setOnClickListener(v -> {
                    if (reviewClickListener != null) {
                        reviewClickListener.onReviewClick(item);
                    }
                });
            } else {
                binding.btnReview.setVisibility(View.GONE);
            }
        }
    }
}

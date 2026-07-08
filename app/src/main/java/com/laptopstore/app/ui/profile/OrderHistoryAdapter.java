package com.laptopstore.app.ui.profile;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laptopstore.app.data.model.order.Order;
import com.laptopstore.app.databinding.ItemOrderHistoryBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orders = new ArrayList<>();

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderHistoryBinding binding = ItemOrderHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderHistoryBinding binding;

        public OrderViewHolder(ItemOrderHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            binding.tvOrderCode.setText(order.getOrderCode());
            binding.tvOrderStatus.setText(order.getStatus());
            
            // Format amount
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.tvOrderAmount.setText(format.format(order.getTotalAmount()));

            if (order.getCreatedAt() != null) {
                // simple substring to display date only
                String date = order.getCreatedAt().length() >= 10 ? order.getCreatedAt().substring(0, 10) : order.getCreatedAt();
                binding.tvOrderDate.setText(date);
            }

            // Display product names
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                StringBuilder productNames = new StringBuilder();
                productNames.append(order.getItems().get(0).getProductName());
                if (order.getItems().size() > 1) {
                    productNames.append(" và ").append(order.getItems().size() - 1).append(" sản phẩm khác");
                }
                binding.tvProductNames.setText(productNames.toString());
                binding.tvProductNames.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.tvProductNames.setVisibility(android.view.View.GONE);
            }

            // Remove root click listener, use buttons instead
            binding.getRoot().setOnClickListener(null);

            binding.btnDetails.setOnClickListener(v -> {
                android.os.Bundle bundle = new android.os.Bundle();
                bundle.putLong("orderId", order.getId());
                androidx.navigation.Navigation.findNavController(v).navigate(com.laptopstore.app.R.id.action_orderHistoryFragment_to_userOrderDetailFragment, bundle);
            });

            if ("DELIVERED".equalsIgnoreCase(order.getStatus())) {
                binding.btnReview.setVisibility(android.view.View.VISIBLE);
                binding.btnReview.setOnClickListener(v -> {
                    if (order.getItems() != null && order.getItems().size() == 1) {
                        android.os.Bundle bundle = new android.os.Bundle();
                        bundle.putLong("productId", order.getItems().get(0).getProductId());
                        bundle.putString("productName", order.getItems().get(0).getProductName());
                        androidx.navigation.Navigation.findNavController(v).navigate(com.laptopstore.app.R.id.writeReviewFragment, bundle);
                    } else {
                        android.os.Bundle bundle = new android.os.Bundle();
                        bundle.putLong("orderId", order.getId());
                        androidx.navigation.Navigation.findNavController(v).navigate(com.laptopstore.app.R.id.action_orderHistoryFragment_to_userOrderDetailFragment, bundle);
                        android.widget.Toast.makeText(v.getContext(), "Vui lòng chọn sản phẩm để đánh giá", android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                binding.btnReview.setVisibility(android.view.View.GONE);
            }
        }
    }
}

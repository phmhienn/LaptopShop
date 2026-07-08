package com.laptopstore.app.ui.admin.order;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laptopstore.app.data.model.order.Order;
import com.laptopstore.app.databinding.ItemAdminOrderBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {

    private final List<Order> orderList = new ArrayList<>();
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public AdminOrderAdapter(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        orderList.clear();
        if (orders != null) {
            orderList.addAll(orders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminOrderBinding binding = ItemAdminOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orderList.get(position));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminOrderBinding binding;

        public OrderViewHolder(@NonNull ItemAdminOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            binding.getRoot().setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onOrderClick(orderList.get(pos));
                }
            });
        }

        public void bind(Order order) {
            binding.tvOrderCode.setText(order.getOrderCode() != null ? order.getOrderCode() : "#" + order.getId());
            binding.tvOrderStatus.setText(order.getStatus());
            binding.tvOrderDate.setText("Date: " + order.getCreatedAt());

            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.tvOrderTotal.setText("Total: " + format.format(order.getFinalAmount() != null ? order.getFinalAmount() : 0));
        }
    }
}

package com.laptopstore.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.laptopstore.app.R;
import com.laptopstore.app.data.model.order.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface CartActionListener {
        void onQuantityChanged(Long itemId, int newQuantity);
        void onItemDeleted(Long itemId);
    }

    private List<CartItem> cartItems = new ArrayList<>();
    private CartActionListener listener;

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCartActionListener(CartActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(cartItems.get(position));
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    class CartViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivImage;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvQuantity;
        private final TextView tvSubtotal;
        private final ImageButton btnDecrease;
        private final ImageButton btnIncrease;
        private final ImageButton btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage      = itemView.findViewById(R.id.iv_cart_item_image);
            tvName       = itemView.findViewById(R.id.tv_cart_item_name);
            tvPrice      = itemView.findViewById(R.id.tv_cart_item_price);
            tvQuantity   = itemView.findViewById(R.id.tv_cart_item_quantity);
            tvSubtotal   = itemView.findViewById(R.id.tv_cart_item_subtotal);
            btnDecrease  = itemView.findViewById(R.id.btn_decrease_quantity);
            btnIncrease  = itemView.findViewById(R.id.btn_increase_quantity);
            btnDelete    = itemView.findViewById(R.id.btn_delete_item);
        }

        public void bind(CartItem item) {
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            tvName.setText(item.getProductName());
            tvPrice.setText(format.format(item.getPrice() != null ? item.getPrice() : 0));
            tvQuantity.setText(String.valueOf(item.getQuantity() != null ? item.getQuantity() : 1));

            double subtotal = item.getSubtotal() != null
                    ? item.getSubtotal()
                    : (item.getPrice() != null && item.getQuantity() != null
                        ? item.getPrice() * item.getQuantity() : 0);
            tvSubtotal.setText("Subtotal: " + format.format(subtotal));

            if (item.getProductThumbnail() != null && !item.getProductThumbnail().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getProductThumbnail())
                        .placeholder(R.mipmap.ic_launcher)
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.mipmap.ic_launcher);
            }

            // Tăng số lượng
            btnIncrease.setOnClickListener(v -> {
                if (listener != null) {
                    int current = item.getQuantity() != null ? item.getQuantity() : 1;
                    listener.onQuantityChanged(item.getId(), current + 1);
                }
            });

            // Giảm số lượng (min = 1, nếu = 1 thì xóa)
            btnDecrease.setOnClickListener(v -> {
                if (listener != null) {
                    int current = item.getQuantity() != null ? item.getQuantity() : 1;
                    if (current <= 1) {
                        listener.onItemDeleted(item.getId());
                    } else {
                        listener.onQuantityChanged(item.getId(), current - 1);
                    }
                }
            });

            // Xóa item
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemDeleted(item.getId());
                }
            });
        }
    }
}

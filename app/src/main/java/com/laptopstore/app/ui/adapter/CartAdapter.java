package com.laptopstore.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private List<CartItem> cartItems = new ArrayList<>();

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
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

        private ImageView ivImage;
        private TextView tvName;
        private TextView tvPrice;
        private TextView tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_cart_item_image);
            tvName = itemView.findViewById(R.id.tv_cart_item_name);
            tvPrice = itemView.findViewById(R.id.tv_cart_item_price);
            tvQuantity = itemView.findViewById(R.id.tv_cart_item_quantity);
        }

        public void bind(CartItem item) {
            tvName.setText(item.getProductName());
            
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvPrice.setText(format.format(item.getPrice()));
            
            tvQuantity.setText("Quantity: " + item.getQuantity());

            if (item.getProductThumbnail() != null && !item.getProductThumbnail().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getProductThumbnail())
                        .placeholder(R.mipmap.ic_launcher)
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }
}

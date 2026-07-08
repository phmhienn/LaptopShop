package com.laptopstore.app.ui.wishlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.laptopstore.app.data.model.product.Product;
import com.laptopstore.app.databinding.ItemWishlistBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<Product> products = new ArrayList<>();
    private final OnWishlistInteractionListener listener;

    public interface OnWishlistInteractionListener {
        void onProductClick(Product product);
        void onRemoveClick(Product product, int position);
    }

    public WishlistAdapter(OnWishlistInteractionListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void removeProduct(int position) {
        if (position >= 0 && position < products.size()) {
            products.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWishlistBinding binding = ItemWishlistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WishlistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        holder.bind(products.get(position), position);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class WishlistViewHolder extends RecyclerView.ViewHolder {
        private final ItemWishlistBinding binding;

        public WishlistViewHolder(ItemWishlistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product product, int position) {
            binding.tvProductName.setText(product.getName());
            
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.tvProductPrice.setText(format.format(product.getPrice()));

            if (product.getThumbnail() != null && !product.getThumbnail().isEmpty()) {
                Glide.with(binding.getRoot())
                     .load(product.getThumbnail())
                     .into(binding.ivProductImage);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            binding.ivRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveClick(product, getAdapterPosition());
                }
            });
        }
    }
}

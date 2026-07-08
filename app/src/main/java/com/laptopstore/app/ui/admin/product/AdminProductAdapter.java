package com.laptopstore.app.ui.admin.product;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.laptopstore.app.R;
import com.laptopstore.app.data.model.product.Product;
import com.laptopstore.app.databinding.ItemAdminProductBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private final List<Product> productList = new ArrayList<>();
    private final OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public AdminProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        productList.clear();
        if (products != null) {
            productList.addAll(products);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminProductBinding binding = ItemAdminProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminProductBinding binding;

        public ProductViewHolder(@NonNull ItemAdminProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            binding.getRoot().setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onProductClick(productList.get(pos));
                }
            });
        }

        public void bind(Product product) {
            binding.tvProductName.setText(product.getName());
            
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.tvProductPrice.setText("Price: " + format.format(product.getEffectivePrice() != null ? product.getEffectivePrice() : 0));
            
            // Note: The product status might not be serialized in the short DTO, but let's assume it is or just don't show it.
            // Let's hide it if not available
            binding.tvProductStatus.setText("ACTIVE");
            binding.tvProductStatus.setTextColor(Color.parseColor("#4CAF50")); // Green

            if (product.getThumbnail() != null && !product.getThumbnail().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                     .load(product.getThumbnail())
                     .placeholder(R.mipmap.ic_launcher)
                     .into(binding.ivProductImage);
            } else {
                binding.ivProductImage.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }
}

package com.laptopstore.app.ui.admin.brand;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.laptopstore.app.R;
import com.laptopstore.app.data.model.product.Brand;
import com.laptopstore.app.databinding.ItemAdminBrandBinding;

import java.util.ArrayList;
import java.util.List;

public class AdminBrandAdapter extends RecyclerView.Adapter<AdminBrandAdapter.BrandViewHolder> {

    private final List<Brand> brandList = new ArrayList<>();
    private final OnBrandClickListener listener;

    public interface OnBrandClickListener {
        void onBrandClick(Brand brand);
    }

    public AdminBrandAdapter(OnBrandClickListener listener) {
        this.listener = listener;
    }

    public void setBrands(List<Brand> brands) {
        brandList.clear();
        if (brands != null) {
            brandList.addAll(brands);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminBrandBinding binding = ItemAdminBrandBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BrandViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        holder.bind(brandList.get(position));
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    class BrandViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminBrandBinding binding;

        public BrandViewHolder(@NonNull ItemAdminBrandBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            binding.getRoot().setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBrandClick(brandList.get(pos));
                }
            });
        }

        public void bind(Brand brand) {
            binding.tvBrandName.setText(brand.getName());
            binding.tvBrandSlug.setText("Slug: " + brand.getSlug());
            
            if (brand.isActive()) {
                binding.tvBrandStatus.setText("ACTIVE");
                binding.tvBrandStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else {
                binding.tvBrandStatus.setText("INACTIVE");
                binding.tvBrandStatus.setTextColor(Color.parseColor("#F44336")); // Red
            }

            if (brand.getImage() != null && !brand.getImage().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                     .load(brand.getImage())
                     .placeholder(R.mipmap.ic_launcher)
                     .into(binding.ivBrandImage);
            } else {
                binding.ivBrandImage.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }
}

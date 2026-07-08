package com.laptopstore.app.ui.admin.category;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.laptopstore.app.R;
import com.laptopstore.app.data.model.product.Category;
import com.laptopstore.app.databinding.ItemAdminCategoryBinding;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.CategoryViewHolder> {

    private final List<Category> categoryList = new ArrayList<>();
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public AdminCategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        categoryList.clear();
        if (categories != null) {
            categoryList.addAll(categories);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminCategoryBinding binding = ItemAdminCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminCategoryBinding binding;

        public CategoryViewHolder(@NonNull ItemAdminCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            binding.getRoot().setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCategoryClick(categoryList.get(pos));
                }
            });
        }

        public void bind(Category category) {
            binding.tvCategoryName.setText(category.getName());
            binding.tvCategorySlug.setText("Slug: " + category.getSlug());
            
            if (category.isActive()) {
                binding.tvCategoryStatus.setText("ACTIVE");
                binding.tvCategoryStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else {
                binding.tvCategoryStatus.setText("INACTIVE");
                binding.tvCategoryStatus.setTextColor(Color.parseColor("#F44336")); // Red
            }

            if (category.getImage() != null && !category.getImage().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                     .load(category.getImage())
                     .placeholder(R.mipmap.ic_launcher)
                     .into(binding.ivCategoryImage);
            } else {
                binding.ivCategoryImage.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }
}

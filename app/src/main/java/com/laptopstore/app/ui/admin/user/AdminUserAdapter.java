package com.laptopstore.app.ui.admin.user;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.laptopstore.app.R;
import com.laptopstore.app.data.model.user.User;
import com.laptopstore.app.databinding.ItemAdminUserBinding;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private final List<User> userList = new ArrayList<>();
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public AdminUserAdapter(OnUserClickListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        userList.clear();
        if (users != null) {
            userList.addAll(users);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminUserBinding binding = ItemAdminUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminUserBinding binding;

        public UserViewHolder(@NonNull ItemAdminUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            binding.getRoot().setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onUserClick(userList.get(pos));
                }
            });
        }

        public void bind(User user) {
            binding.tvUsername.setText(user.getUsername());
            binding.tvEmail.setText(user.getEmail());
            
            if (user.getRoles() != null) {
                binding.tvRoles.setText("Roles: " + TextUtils.join(", ", user.getRoles()));
            } else {
                binding.tvRoles.setText("Roles: USER");
            }

            if (user.isEnabled()) {
                binding.tvStatus.setText("ACTIVE");
                binding.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else {
                binding.tvStatus.setText("BLOCKED");
                binding.tvStatus.setTextColor(Color.parseColor("#F44336")); // Red
            }

            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                     .load(user.getAvatar())
                     .placeholder(R.mipmap.ic_launcher)
                     .circleCrop()
                     .into(binding.ivAvatar);
            } else {
                binding.ivAvatar.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }
}

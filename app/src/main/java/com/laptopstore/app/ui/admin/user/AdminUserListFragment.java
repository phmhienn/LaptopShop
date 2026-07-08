package com.laptopstore.app.ui.admin.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.laptopstore.app.R;
import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.PagedResponse;
import com.laptopstore.app.data.model.user.User;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentAdminUserListBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserListFragment extends Fragment {

    private FragmentAdminUserListBinding binding;
    private AdminUserAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminUserListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers();
    }

    private void setupRecyclerView() {
        adapter = new AdminUserAdapter(user -> {
            Bundle bundle = new Bundle();
            bundle.putLong("userId", user.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_adminUserListFragment_to_adminUserDetailFragment, bundle);
        });
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvUsers.setAdapter(adapter);
    }

    private void loadUsers() {
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService(requireContext()).getAllUsers("", 0, 50).enqueue(new Callback<ApiResponse<PagedResponse<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResponse<User>>> call, Response<ApiResponse<PagedResponse<User>>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.setUsers(response.body().getData().getContent());
                } else {
                    showToast("Failed to load users");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResponse<User>>> call, Throwable t) {
                if (binding != null) {
                    binding.progressBar.setVisibility(View.GONE);
                }
                showToast("Network Error: " + t.getMessage());
            }
        });
    }

    private void showToast(String msg) {
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

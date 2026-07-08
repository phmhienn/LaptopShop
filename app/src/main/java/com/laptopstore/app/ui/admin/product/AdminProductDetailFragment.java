package com.laptopstore.app.ui.admin.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.PagedResponse;
import com.laptopstore.app.data.model.product.Brand;
import com.laptopstore.app.data.model.product.Category;
import com.laptopstore.app.data.model.product.ProductDetail;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.databinding.FragmentAdminProductDetailBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductDetailFragment extends Fragment {

    private FragmentAdminProductDetailBinding binding;
    private Long productId = -1L;
    private boolean isEditMode = false;

    private List<Category> categories = new ArrayList<>();
    private List<Brand> brands = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            productId = getArguments().getLong("productId", -1L);
            isEditMode = productId != -1L;
        }

        if (isEditMode) {
            binding.tvTitle.setText("Edit Product");
            binding.btnToggleStatus.setVisibility(View.VISIBLE);
        } else {
            binding.tvTitle.setText("Add New Product");
            binding.btnToggleStatus.setVisibility(View.GONE);
        }

        loadFormData();

        binding.btnSave.setOnClickListener(v -> saveProduct());
        binding.btnToggleStatus.setOnClickListener(v -> toggleStatus());
    }

    private void loadFormData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // 1. Load Categories
        ApiClient.getApiService(requireContext()).getAllCategories(0, 100).enqueue(new Callback<ApiResponse<PagedResponse<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResponse<Category>>> call, Response<ApiResponse<PagedResponse<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body().getData().getContent();
                    setupCategorySpinner();
                    
                    // 2. Load Brands
                    loadBrands();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResponse<Category>>> call, Throwable t) {
                showToast("Failed to load categories");
            }
        });
    }

    private void loadBrands() {
        ApiClient.getApiService(requireContext()).getAllBrands(0, 100).enqueue(new Callback<ApiResponse<PagedResponse<Brand>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResponse<Brand>>> call, Response<ApiResponse<PagedResponse<Brand>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    brands = response.body().getData().getContent();
                    setupBrandSpinner();
                    
                    // 3. Load Product details if edit mode
                    if (isEditMode) {
                        loadProductDetails();
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResponse<Brand>>> call, Throwable t) {
                showToast("Failed to load brands");
            }
        });
    }

    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        for (Category c : categories) categoryNames.add(c.getName());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryNames);
        binding.spinnerCategory.setAdapter(adapter);
    }

    private void setupBrandSpinner() {
        List<String> brandNames = new ArrayList<>();
        for (Brand b : brands) brandNames.add(b.getName());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, brandNames);
        binding.spinnerBrand.setAdapter(adapter);
    }

    private void loadProductDetails() {
        ApiClient.getApiService(requireContext()).getProductById(productId).enqueue(new Callback<ApiResponse<ProductDetail>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductDetail>> call, Response<ApiResponse<ProductDetail>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ProductDetail product = response.body().getData();
                    
                    binding.etName.setText(product.getName());
                    binding.etDescription.setText(product.getDescription());
                    binding.etPrice.setText(String.valueOf(product.getPrice() != null ? product.getPrice() : ""));
                    binding.etDiscount.setText(String.valueOf(product.getDiscountPrice() != null ? product.getDiscountPrice() : ""));
                    
                    binding.etCpu.setText(product.getCpu());
                    binding.etRam.setText(product.getRam());
                    binding.etSsd.setText(product.getSsd());
                    
                    // Note: ProductDetailDTO on backend has CategoryDTO and BrandDTO, 
                    // but our app ProductDetail doesn't have brand/category fields fully populated in the short version.
                    // For the sake of simplicity, we don't auto-select Spinners.

                } else {
                    showToast("Failed to load product details");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductDetail>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                showToast("Error: " + t.getMessage());
            }
        });
    }

    private void saveProduct() {
        String name = binding.etName.getText().toString().trim();
        if (name.isEmpty()) {
            binding.etName.setError("Name is required");
            return;
        }

        // Prepare JSON payload
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("name", name);
        productMap.put("description", binding.etDescription.getText().toString().trim());
        
        try {
            productMap.put("price", Double.parseDouble(binding.etPrice.getText().toString()));
            String discountStr = binding.etDiscount.getText().toString();
            if (!discountStr.isEmpty()) productMap.put("discountPrice", Double.parseDouble(discountStr));
            
            productMap.put("stock", Integer.parseInt(binding.etStock.getText().toString()));
            productMap.put("warranty", Integer.parseInt(binding.etWarranty.getText().toString()));
        } catch (NumberFormatException e) {
            showToast("Invalid number format for price/stock");
            return;
        }

        productMap.put("cpu", binding.etCpu.getText().toString());
        productMap.put("ram", binding.etRam.getText().toString());
        productMap.put("ssd", binding.etSsd.getText().toString());
        productMap.put("featured", binding.switchFeatured.isChecked());

        String productJson = new Gson().toJson(productMap);

        Long categoryId = categories.get(binding.spinnerCategory.getSelectedItemPosition()).getId();
        Long brandId = brands.get(binding.spinnerBrand.getSelectedItemPosition()).getId();

        RequestBody productBody = RequestBody.create(MediaType.parse("text/plain"), productJson);
        RequestBody categoryBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(categoryId));
        RequestBody brandBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(brandId));

        binding.progressBar.setVisibility(View.VISIBLE);

        if (isEditMode) {
            ApiClient.getApiService(requireContext()).updateProduct(productId, productBody, brandBody, categoryBody).enqueue(saveCallback);
        } else {
            ApiClient.getApiService(requireContext()).createProduct(productBody, brandBody, categoryBody).enqueue(saveCallback);
        }
    }

    private final Callback<ApiResponse<ProductDetail>> saveCallback = new Callback<ApiResponse<ProductDetail>>() {
        @Override
        public void onResponse(Call<ApiResponse<ProductDetail>> call, Response<ApiResponse<ProductDetail>> response) {
            binding.progressBar.setVisibility(View.GONE);
            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                showToast("Product saved successfully");
                if (getActivity() != null) getActivity().onBackPressed();
            } else {
                showToast("Failed to save product");
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<ProductDetail>> call, Throwable t) {
            if (binding != null) binding.progressBar.setVisibility(View.GONE);
            showToast("Error: " + t.getMessage());
        }
    };

    private void toggleStatus() {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService(requireContext()).toggleProductStatus(productId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    showToast("Product status toggled");
                } else {
                    showToast("Failed to toggle status");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                if (binding != null) binding.progressBar.setVisibility(View.GONE);
                showToast("Error: " + t.getMessage());
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

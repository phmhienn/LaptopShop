package com.laptopstore.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.laptopstore.app.R;
import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.product.Brand;
import com.laptopstore.app.data.model.product.Category;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.data.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFilterFragment extends Fragment {

    private TextInputEditText etKeyword, etMinPrice, etMaxPrice;
    private Spinner spinnerBrand, spinnerCategory, spinnerRam, spinnerCpu;
    private Button btnApplyFilter;

    private ApiService apiService;
    private List<Brand> brandList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etKeyword = view.findViewById(R.id.et_keyword);
        etMinPrice = view.findViewById(R.id.et_min_price);
        etMaxPrice = view.findViewById(R.id.et_max_price);
        spinnerBrand = view.findViewById(R.id.spinner_brand);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        spinnerRam = view.findViewById(R.id.spinner_ram);
        spinnerCpu = view.findViewById(R.id.spinner_cpu);
        btnApplyFilter = view.findViewById(R.id.btn_apply_filter);

        apiService = ApiClient.getApiService(getContext());

        setupSpinners();
        loadBrandsAndCategories();

        btnApplyFilter.setOnClickListener(v -> applyFilter(view));
    }

    private void setupSpinners() {
        // RAM Setup
        String[] ramOptions = {"Any", "4GB", "8GB", "16GB", "32GB", "64GB"};
        ArrayAdapter<String> ramAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, ramOptions);
        ramAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRam.setAdapter(ramAdapter);

        // CPU Setup
        String[] cpuOptions = {"Any", "Intel Core i3", "Intel Core i5", "Intel Core i7", "Intel Core i9", "AMD Ryzen 3", "AMD Ryzen 5", "AMD Ryzen 7", "AMD Ryzen 9", "Apple M1", "Apple M2", "Apple M3"};
        ArrayAdapter<String> cpuAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cpuOptions);
        cpuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCpu.setAdapter(cpuAdapter);
    }

    private void loadBrandsAndCategories() {
        // Load Brands
        apiService.getBrands().enqueue(new Callback<ApiResponse<List<Brand>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Brand>>> call, Response<ApiResponse<List<Brand>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    brandList = response.body().getData();
                    List<String> brandNames = new ArrayList<>();
                    brandNames.add("Any Brand");
                    for (Brand b : brandList) {
                        brandNames.add(b.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, brandNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBrand.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Brand>>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load brands", Toast.LENGTH_SHORT).show();
            }
        });

        // Load Categories
        apiService.getCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body().getData();
                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add("Any Category");
                    for (Category c : categoryList) {
                        categoryNames.add(c.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter(View view) {
        String keyword = etKeyword.getText() != null ? etKeyword.getText().toString() : "";
        String minPriceStr = etMinPrice.getText() != null ? etMinPrice.getText().toString() : "";
        String maxPriceStr = etMaxPrice.getText() != null ? etMaxPrice.getText().toString() : "";
        
        Long brandId = null;
        if (spinnerBrand.getSelectedItemPosition() > 0 && spinnerBrand.getSelectedItemPosition() <= brandList.size()) {
            brandId = brandList.get(spinnerBrand.getSelectedItemPosition() - 1).getId();
        }

        Long categoryId = null;
        if (spinnerCategory.getSelectedItemPosition() > 0 && spinnerCategory.getSelectedItemPosition() <= categoryList.size()) {
            categoryId = categoryList.get(spinnerCategory.getSelectedItemPosition() - 1).getId();
        }

        String ram = "";
        if (spinnerRam.getSelectedItemPosition() > 0) {
            ram = spinnerRam.getSelectedItem().toString();
        }

        String cpu = "";
        if (spinnerCpu.getSelectedItemPosition() > 0) {
            cpu = spinnerCpu.getSelectedItem().toString();
        }

        Bundle args = new Bundle();
        args.putString("keyword", keyword);
        args.putString("minPrice", minPriceStr);
        args.putString("maxPrice", maxPriceStr);
        if (brandId != null) args.putLong("brandId", brandId);
        if (categoryId != null) args.putLong("categoryId", categoryId);
        args.putString("ram", ram);
        args.putString("cpu", cpu);

        Navigation.findNavController(view).navigate(R.id.action_searchFilter_to_searchResults, args);
    }
}

package com.laptopstore.app.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.laptopstore.app.R;
import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.PagedResponse;
import com.laptopstore.app.data.model.product.Product;
import com.laptopstore.app.data.network.ApiClient;
import com.laptopstore.app.data.network.ApiService;
import com.laptopstore.app.ui.adapter.ProductAdapter;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsFragment extends Fragment {

    private RecyclerView rvSearchResults;
    private ProgressBar progressBar;
    private TextView tvSearchSummary, tvNoResults;
    private ProductAdapter productAdapter;
    private ApiService apiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSearchResults = view.findViewById(R.id.rv_search_results);
        progressBar = view.findViewById(R.id.progress_bar);
        tvSearchSummary = view.findViewById(R.id.tv_search_summary);
        tvNoResults = view.findViewById(R.id.tv_no_results);

        apiService = ApiClient.getApiService(getContext());

        setupRecyclerView();
        loadSearchResults();
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter();
        rvSearchResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvSearchResults.setAdapter(productAdapter);

        productAdapter.setOnProductClickListener(product -> {
            Bundle bundle = new Bundle();
            bundle.putLong("productId", product.getId());
            androidx.navigation.Navigation.findNavController(getView())
                .navigate(R.id.action_searchResults_to_productDetailFragment, bundle);
        });
    }

    private void loadSearchResults() {
        progressBar.setVisibility(View.VISIBLE);
        rvSearchResults.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.GONE);

        Bundle args = getArguments();
        String keyword = args != null ? args.getString("keyword") : null;
        String minPriceStr = args != null ? args.getString("minPrice") : null;
        String maxPriceStr = args != null ? args.getString("maxPrice") : null;
        Long brandId = args != null && args.containsKey("brandId") ? args.getLong("brandId") : null;
        Long categoryId = args != null && args.containsKey("categoryId") ? args.getLong("categoryId") : null;
        String ram = args != null ? args.getString("ram") : null;
        String cpu = args != null ? args.getString("cpu") : null;

        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;

        if (minPriceStr != null && !minPriceStr.isEmpty()) {
            try {
                minPrice = new BigDecimal(minPriceStr);
            } catch (Exception e) { e.printStackTrace(); }
        }

        if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
            try {
                maxPrice = new BigDecimal(maxPriceStr);
            } catch (Exception e) { e.printStackTrace(); }
        }

        if ("Any".equals(ram)) ram = null;
        if ("Any".equals(cpu)) cpu = null;

        apiService.searchProducts(keyword, brandId, categoryId, minPrice, maxPrice, ram, cpu, 0, 50).enqueue(new Callback<ApiResponse<PagedResponse<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PagedResponse<Product>>> call, Response<ApiResponse<PagedResponse<Product>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<Product> pagedResponse = response.body().getData();
                    if (pagedResponse != null && pagedResponse.getContent() != null && !pagedResponse.getContent().isEmpty()) {
                        productAdapter.setProducts(pagedResponse.getContent());
                        rvSearchResults.setVisibility(View.VISIBLE);
                        tvSearchSummary.setText("Found " + pagedResponse.getTotalElements() + " results");
                    } else {
                        tvNoResults.setVisibility(View.VISIBLE);
                        tvSearchSummary.setText("No results found");
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to search products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PagedResponse<Product>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

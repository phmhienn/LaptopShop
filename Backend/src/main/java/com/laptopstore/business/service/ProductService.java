package com.laptopstore.business.service;

import com.laptopstore.data.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {
    
    Product createProduct(Product product, Long brandId, Long categoryId, MultipartFile thumbnail, List<MultipartFile> images);
    
    Product updateProduct(Long id, Product productDetails, Long brandId, Long categoryId, MultipartFile thumbnail);
    
    Product getProductById(Long id);
    
    Product getProductBySlug(String slug);
    
    void deleteProduct(Long id);
    
    void toggleProductStatus(Long id);
    
    Page<Product> searchProducts(String keyword, Long brandId, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String ram, String cpu, Pageable pageable);
    
    Page<Product> getProductsByCategory(Long categoryId, Pageable pageable);
    
    Page<Product> getProductsByBrand(Long brandId, Pageable pageable);
    
    Page<Product> getFeaturedProducts(Pageable pageable);
    
    Page<Product> getLatestProducts(Pageable pageable);
    
    Page<Product> filterProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Image management
    Product addProductImages(Long productId, List<MultipartFile> images);
    void removeProductImage(Long productId, Long imageId);
    
    // Inventory
    void updateInventory(Long productId, int quantity);

    // Batch update inventory — tránh N+1 query trong loop
    void batchUpdateInventory(Map<Long, Integer> productQuantities);
}

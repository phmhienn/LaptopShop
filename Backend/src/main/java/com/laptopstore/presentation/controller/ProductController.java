package com.laptopstore.presentation.controller;

import com.laptopstore.application.dto.product.ProductDTO;
import com.laptopstore.application.dto.product.ProductDetailDTO;
import com.laptopstore.application.facade.ProductFacade;
import com.laptopstore.common.constants.AppConstants;
import com.laptopstore.common.response.ApiResponse;
import com.laptopstore.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;

    // Public endpoints
    
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> searchProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "ram", required = false) String ram,
            @RequestParam(value = "cpu", required = false) String cpu,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {
        
        PagedResponse<ProductDTO> response = productFacade.searchProducts(
                keyword, brandId, categoryId, minPrice, maxPrice, ram, cpu, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {
        
        PagedResponse<ProductDTO> response = productFacade.getProductsByCategory(categoryId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> getProductsByBrand(
            @PathVariable Long brandId,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {
        
        PagedResponse<ProductDTO> response = productFacade.getProductsByBrand(brandId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> getFeaturedProducts(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        
        PagedResponse<ProductDTO> response = productFacade.getFeaturedProducts(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> getLatestProducts(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        
        PagedResponse<ProductDTO> response = productFacade.getLatestProducts(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<PagedResponse<ProductDTO>>> filterProductsByPrice(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        
        PagedResponse<ProductDTO> response = productFacade.filterProductsByPrice(minPrice, maxPrice, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getProductBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(productFacade.getProductBySlug(slug)));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productFacade.getProductById(id)));
    }

    // Admin endpoints

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> createProduct(
            @RequestParam("product") String productJson,
            @RequestParam("brandId") Long brandId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        
        ProductDetailDTO createdProduct = productFacade.createProduct(productJson, brandId, categoryId, thumbnail, images);
        return ResponseEntity.ok(ApiResponse.success("Product created successfully", createdProduct));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> updateProduct(
            @PathVariable Long id,
            @RequestParam("product") String productJson,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail) {
        
        ProductDetailDTO updatedProduct = productFacade.updateProduct(id, productJson, brandId, categoryId, thumbnail);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", updatedProduct));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productFacade.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleProductStatus(@PathVariable Long id) {
        productFacade.toggleProductStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Product status toggled successfully"));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDetailDTO>> addProductImages(
            @PathVariable Long id,
            @RequestParam("images") List<MultipartFile> images) {
        
        ProductDetailDTO updatedProduct = productFacade.addProductImages(id, images);
        return ResponseEntity.ok(ApiResponse.success("Images added successfully", updatedProduct));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeProductImage(
            @PathVariable Long id,
            @PathVariable Long imageId) {
        
        productFacade.removeProductImage(id, imageId);
        return ResponseEntity.ok(ApiResponse.success("Image removed successfully"));
    }

    @PatchMapping("/{id}/inventory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateInventory(
            @PathVariable Long id,
            @RequestParam("quantity") int quantity) {
        
        productFacade.updateInventory(id, quantity);
        return ResponseEntity.ok(ApiResponse.success("Inventory updated successfully"));
    }
}

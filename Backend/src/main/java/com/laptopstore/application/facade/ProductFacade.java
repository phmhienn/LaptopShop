package com.laptopstore.application.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptopstore.application.dto.product.ProductDTO;
import com.laptopstore.application.dto.product.ProductDetailDTO;
import com.laptopstore.application.mapper.ProductMapper;
import com.laptopstore.business.exception.ValidationException;
import com.laptopstore.business.service.ProductService;
import com.laptopstore.common.response.PagedResponse;
import com.laptopstore.data.entity.Product;
import com.laptopstore.data.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductFacade {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ProductDetailDTO createProduct(String productJson, Long brandId, Long categoryId, 
                                        MultipartFile thumbnail, List<MultipartFile> images) {
        try {
            Product product = objectMapper.readValue(productJson, Product.class);
            Product createdProduct = productService.createProduct(product, brandId, categoryId, thumbnail, images);
            return mapToDetailDTO(createdProduct);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Invalid product data format: " + e.getMessage());
        }
    }

    @Transactional
    public ProductDetailDTO updateProduct(Long id, String productJson, Long brandId, Long categoryId, 
                                        MultipartFile thumbnail) {
        try {
            Product productDetails = objectMapper.readValue(productJson, Product.class);
            Product updatedProduct = productService.updateProduct(id, productDetails, brandId, categoryId, thumbnail);
            return mapToDetailDTO(updatedProduct);
        } catch (JsonProcessingException e) {
            throw new ValidationException("Invalid product data format: " + e.getMessage());
        }
    }

    public ProductDetailDTO getProductById(Long id) {
        return mapToDetailDTO(productService.getProductById(id));
    }

    public ProductDetailDTO getProductBySlug(String slug) {
        return mapToDetailDTO(productService.getProductBySlug(slug));
    }

    @Transactional
    public void deleteProduct(Long id) {
        productService.deleteProduct(id);
    }

    @Transactional
    public void toggleProductStatus(Long id) {
        productService.toggleProductStatus(id);
    }

    public PagedResponse<ProductDTO> searchProducts(String keyword, Long brandId, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String ram, String cpu, int page, int size, String sortBy, String sortDir) {
        Page<Product> productsPage = productService.searchProducts(keyword, brandId, categoryId, minPrice, maxPrice, ram, cpu, getPageable(page, size, sortBy, sortDir));
        return mapToPagedResponse(productsPage);
    }

    public PagedResponse<ProductDTO> getProductsByCategory(Long categoryId, int page, int size, String sortBy, String sortDir) {
        Page<Product> productsPage = productService.getProductsByCategory(categoryId, getPageable(page, size, sortBy, sortDir));
        return mapToPagedResponse(productsPage);
    }

    public PagedResponse<ProductDTO> getProductsByBrand(Long brandId, int page, int size, String sortBy, String sortDir) {
        Page<Product> productsPage = productService.getProductsByBrand(brandId, getPageable(page, size, sortBy, sortDir));
        return mapToPagedResponse(productsPage);
    }

    public PagedResponse<ProductDTO> getFeaturedProducts(int page, int size) {
        Page<Product> productsPage = productService.getFeaturedProducts(getPageable(page, size, "createdAt", "desc"));
        return mapToPagedResponse(productsPage);
    }

    public PagedResponse<ProductDTO> getLatestProducts(int page, int size) {
        Page<Product> productsPage = productService.getLatestProducts(getPageable(page, size, "createdAt", "desc"));
        return mapToPagedResponse(productsPage);
    }

    public PagedResponse<ProductDTO> filterProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        Page<Product> productsPage = productService.filterProductsByPrice(minPrice, maxPrice, getPageable(page, size, "price", "asc"));
        return mapToPagedResponse(productsPage);
    }

    @Transactional
    public ProductDetailDTO addProductImages(Long productId, List<MultipartFile> images) {
        return mapToDetailDTO(productService.addProductImages(productId, images));
    }

    @Transactional
    public void removeProductImage(Long productId, Long imageId) {
        productService.removeProductImage(productId, imageId);
    }

    @Transactional
    public void updateInventory(Long productId, int quantity) {
        productService.updateInventory(productId, quantity);
    }

    // Helper methods
    
    private Pageable getPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private ProductDetailDTO mapToDetailDTO(Product product) {
        Double avgRating = reviewRepository.getAverageRatingByProductId(product.getId());
        Long totalReviews = reviewRepository.countApprovedReviewsByProductId(product.getId());
        return productMapper.toProductDetailDTO(product, avgRating, totalReviews);
    }

    private ProductDTO mapToDTO(Product product) {
        Double avgRating = reviewRepository.getAverageRatingByProductId(product.getId());
        Long totalReviews = reviewRepository.countApprovedReviewsByProductId(product.getId());
        return productMapper.toProductDTO(product, avgRating, totalReviews);
    }

    private PagedResponse<ProductDTO> mapToPagedResponse(Page<Product> productsPage) {
        List<ProductDTO> content = productsPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PagedResponse.of(content, productsPage.getNumber(), productsPage.getSize(),
                productsPage.getTotalElements(), productsPage.getTotalPages());
    }
}

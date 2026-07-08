package com.laptopstore.business.service.impl;

import com.laptopstore.business.exception.ResourceNotFoundException;
import com.laptopstore.business.exception.ValidationException;
import com.laptopstore.business.service.BrandService;
import com.laptopstore.business.service.CategoryService;
import com.laptopstore.business.service.FileStorageService;
import com.laptopstore.business.service.ProductService;
import com.laptopstore.common.constants.AppConstants;
import com.laptopstore.common.enums.ProductStatus;
import com.laptopstore.common.utils.SlugUtils;
import com.laptopstore.data.entity.Brand;
import com.laptopstore.data.entity.Category;
import com.laptopstore.data.entity.Inventory;
import com.laptopstore.data.entity.Product;
import com.laptopstore.data.entity.ProductImage;
import com.laptopstore.data.repository.InventoryRepository;
import com.laptopstore.data.repository.ProductImageRepository;
import com.laptopstore.data.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final InventoryRepository inventoryRepository;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public Product createProduct(Product product, Long brandId, Long categoryId, MultipartFile thumbnail, List<MultipartFile> images) {
        product.setSlug(SlugUtils.toSlug(product.getName()));
        if (productRepository.existsBySlug(product.getSlug())) {
            product.setSlug(product.getSlug() + "-" + System.currentTimeMillis());
        }

        Brand brand = brandService.getBrandById(brandId);
        Category category = categoryService.getCategoryById(categoryId);
        
        product.setBrand(brand);
        product.setCategory(category);
        
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String fileName = fileStorageService.storeFile(thumbnail, AppConstants.PRODUCT_IMAGE_UPLOAD_DIR);
            product.setThumbnail(fileName);
        }

        Product savedProduct = productRepository.save(product);

        // Initialize Inventory
        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setQuantity(0); // Initial quantity
        inventoryRepository.save(inventory);

        // Save Additional Images
        if (images != null && !images.isEmpty()) {
            int order = 1;
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String fileName = fileStorageService.storeFile(file, AppConstants.PRODUCT_IMAGE_UPLOAD_DIR);
                    ProductImage productImage = new ProductImage();
                    productImage.setProduct(savedProduct);
                    productImage.setImageUrl(fileName);
                    productImage.setDisplayOrder(order++);
                    productImageRepository.save(productImage);
                }
            }
        }

        return getProductById(savedProduct.getId());
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product productDetails, Long brandId, Long categoryId, MultipartFile thumbnail) {
        Product product = getProductById(id);
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setCpu(productDetails.getCpu());
        product.setRam(productDetails.getRam());
        product.setSsd(productDetails.getSsd());
        product.setGpu(productDetails.getGpu());
        product.setDisplay(productDetails.getDisplay());
        product.setBattery(productDetails.getBattery());
        product.setWeight(productDetails.getWeight());
        product.setWarranty(productDetails.getWarranty());
        product.setPrice(productDetails.getPrice());
        product.setDiscountPrice(productDetails.getDiscountPrice());
        product.setFeatured(productDetails.getFeatured());

        if (brandId != null && !brandId.equals(product.getBrand().getId())) {
            product.setBrand(brandService.getBrandById(brandId));
        }

        if (categoryId != null && !categoryId.equals(product.getCategory().getId())) {
            product.setCategory(categoryService.getCategoryById(categoryId));
        }

        if (thumbnail != null && !thumbnail.isEmpty()) {
            if (product.getThumbnail() != null) {
                fileStorageService.deleteFile(product.getThumbnail(), AppConstants.PRODUCT_IMAGE_UPLOAD_DIR);
            }
            String fileName = fileStorageService.storeFile(thumbnail, AppConstants.PRODUCT_IMAGE_UPLOAD_DIR);
            product.setThumbnail(fileName);
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        
        // Logical delete by changing status
        product.setStatus(ProductStatus.DISCONTINUED);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void toggleProductStatus(Long id) {
        Product product = getProductById(id);
        if (product.getStatus() == ProductStatus.ACTIVE) {
            product.setStatus(ProductStatus.INACTIVE);
        } else {
            product.setStatus(ProductStatus.ACTIVE);
        }
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Long brandId, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String ram, String cpu, Pageable pageable) {
        org.springframework.data.jpa.domain.Specification<Product> spec = 
            com.laptopstore.data.repository.ProductSpecification.getSearchSpecification(keyword, brandId, categoryId, minPrice, maxPrice, ram, cpu);
        return productRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndStatus(categoryId, ProductStatus.ACTIVE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByBrand(Long brandId, Pageable pageable) {
        return productRepository.findByBrandIdAndStatus(brandId, ProductStatus.ACTIVE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getFeaturedProducts(Pageable pageable) {
        return productRepository.findFeaturedProducts(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getLatestProducts(Pageable pageable) {
        return productRepository.findLatestProducts(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> filterProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    @Override
    @Transactional
    public Product addProductImages(Long productId, List<MultipartFile> images) {
        Product product = getProductById(productId);
        
        long currentImageCount = productImageRepository.countByProductId(productId);
        int order = (int) currentImageCount + 1;
        
        for (MultipartFile file : images) {
            if (!file.isEmpty()) {
                String fileName = fileStorageService.storeFile(file, AppConstants.PRODUCT_IMAGE_UPLOAD_DIR);
                ProductImage productImage = new ProductImage();
                productImage.setProduct(product);
                productImage.setImageUrl(fileName);
                productImage.setDisplayOrder(order++);
                productImageRepository.save(productImage);
            }
        }
        
        return product;
    }

    @Override
    @Transactional
    public void removeProductImage(Long productId, Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductImage", "id", imageId));
                
        if (!image.getProduct().getId().equals(productId)) {
            throw new ValidationException("Image does not belong to this product");
        }
        
        fileStorageService.deleteFile(image.getImageUrl(), AppConstants.PRODUCT_IMAGE_UPLOAD_DIR);
        productImageRepository.delete(image);
    }

    @Override
    @Transactional
    public void updateInventory(Long productId, int quantity) {
        if (quantity < 0) {
            throw new ValidationException("Inventory quantity cannot be negative");
        }
        
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
                
        inventory.setQuantity(quantity);
        inventory.setLastRestockedAt(LocalDateTime.now());
        
        Product product = inventory.getProduct();
        product.setStock(quantity);
        
        inventoryRepository.save(inventory);
        productRepository.save(product);
    }
}

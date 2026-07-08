package com.laptopstore.application.mapper;

import com.laptopstore.application.dto.product.ProductDTO;
import com.laptopstore.application.dto.product.ProductDetailDTO;
import com.laptopstore.application.dto.product.ProductImageDTO;
import com.laptopstore.data.entity.Product;
import com.laptopstore.data.entity.ProductImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final BrandMapper brandMapper;
    private final CategoryMapper categoryMapper;

    public ProductDTO toProductDTO(Product product, Double averageRating, Long totalReviews) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setPrice(product.getPrice());
        dto.setDiscountPrice(product.getDiscountPrice());
        dto.setThumbnail(product.getThumbnail());
        
        if (product.getBrand() != null) {
            dto.setBrandName(product.getBrand().getName());
        }
        
        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }
        
        dto.setStatus(product.getStatus().name());
        dto.setFeatured(product.getFeatured());
        dto.setCreatedAt(product.getCreatedAt());
        
        dto.setAverageRating(averageRating != null ? averageRating : 0.0);
        dto.setTotalReviews(totalReviews != null ? totalReviews : 0L);

        return dto;
    }

    public ProductDetailDTO toProductDetailDTO(Product product, Double averageRating, Long totalReviews) {
        if (product == null) {
            return null;
        }

        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setDescription(product.getDescription());
        
        dto.setCpu(product.getCpu());
        dto.setRam(product.getRam());
        dto.setSsd(product.getSsd());
        dto.setGpu(product.getGpu());
        dto.setDisplay(product.getDisplay());
        dto.setBattery(product.getBattery());
        dto.setWeight(product.getWeight());
        dto.setWarranty(product.getWarranty());
        
        dto.setPrice(product.getPrice());
        dto.setDiscountPrice(product.getDiscountPrice());
        dto.setThumbnail(product.getThumbnail());
        
        if (product.getInventory() != null) {
            dto.setStock(product.getInventory().getAvailableQuantity());
        } else {
            dto.setStock(0);
        }
        
        dto.setStatus(product.getStatus().name());
        dto.setFeatured(product.getFeatured());
        
        dto.setBrand(brandMapper.toBrandDTO(product.getBrand()));
        dto.setCategory(categoryMapper.toCategoryDTOWithoutChildren(product.getCategory()));
        
        if (product.getImages() != null) {
            dto.setImages(product.getImages().stream()
                    .map(this::toProductImageDTO)
                    .collect(Collectors.toList()));
        }
        
        dto.setAverageRating(averageRating != null ? averageRating : 0.0);
        dto.setTotalReviews(totalReviews != null ? totalReviews : 0L);

        return dto;
    }

    private ProductImageDTO toProductImageDTO(ProductImage image) {
        if (image == null) {
            return null;
        }

        ProductImageDTO dto = new ProductImageDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setDisplayOrder(image.getDisplayOrder());

        return dto;
    }
}

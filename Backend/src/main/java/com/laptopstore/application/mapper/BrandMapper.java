package com.laptopstore.application.mapper;

import com.laptopstore.application.dto.product.BrandDTO;
import com.laptopstore.data.entity.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {

    public BrandDTO toBrandDTO(Brand brand) {
        if (brand == null) {
            return null;
        }

        BrandDTO dto = new BrandDTO();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setSlug(brand.getSlug());
        dto.setLogo(brand.getLogo());
        dto.setDescription(brand.getDescription());
        dto.setStatus(brand.getStatus());
        dto.setCreatedAt(brand.getCreatedAt());

        return dto;
    }
}

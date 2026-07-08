package com.laptopstore.business.service;

import com.laptopstore.data.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    
    Brand createBrand(Brand brand);
    
    Brand updateBrand(Long id, Brand brandDetails);
    
    Brand getBrandById(Long id);
    
    Brand getBrandBySlug(String slug);
    
    void deleteBrand(Long id);
    
    void toggleBrandStatus(Long id);
    
    Page<Brand> searchBrands(String keyword, Pageable pageable);
    
    List<Brand> getAllActiveBrands();
}

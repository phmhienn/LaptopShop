package com.laptopstore.business.service.impl;

import com.laptopstore.business.exception.ResourceNotFoundException;
import com.laptopstore.business.exception.ValidationException;
import com.laptopstore.business.service.BrandService;
import com.laptopstore.common.utils.SlugUtils;
import com.laptopstore.data.entity.Brand;
import com.laptopstore.data.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public Brand createBrand(Brand brand) {
        if (brandRepository.existsByName(brand.getName())) {
            throw new ValidationException("Brand name already exists");
        }
        
        brand.setSlug(SlugUtils.toSlug(brand.getName()));
        
        if (brandRepository.existsBySlug(brand.getSlug())) {
            brand.setSlug(brand.getSlug() + "-" + System.currentTimeMillis());
        }
        
        return brandRepository.save(brand);
    }

    @Override
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public Brand updateBrand(Long id, Brand brandDetails) {
        Brand brand = getBrandById(id);
        
        if (!brand.getName().equals(brandDetails.getName()) && brandRepository.existsByName(brandDetails.getName())) {
            throw new ValidationException("Brand name already exists");
        }
        
        brand.setName(brandDetails.getName());
        brand.setSlug(SlugUtils.toSlug(brandDetails.getName()));
        brand.setDescription(brandDetails.getDescription());
        
        if (brandDetails.getLogo() != null) {
            brand.setLogo(brandDetails.getLogo());
        }

        if (brandDetails.getStatus() != null) {
            brand.setStatus(brandDetails.getStatus());
        }
        
        return brandRepository.save(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Brand getBrandBySlug(String slug) {
        return brandRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "slug", slug));
    }

    @Override
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public void deleteBrand(Long id) {
        Brand brand = getBrandById(id);

        // Dùng repository count thay vì brand.getProducts() (bị @JsonIgnore lazy-load rủi)
        if (brandRepository.countProductsByBrandId(id) > 0) {
            throw new ValidationException("Cannot delete brand that has products. Consider disabling it instead.");
        }

        brandRepository.delete(brand);
    }

    @Override
    @Transactional
    @CacheEvict(value = "brands", allEntries = true)
    public void toggleBrandStatus(Long id) {
        Brand brand = getBrandById(id);
        brand.setStatus(!brand.getStatus());
        brandRepository.save(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Brand> searchBrands(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return brandRepository.findAll(pageable);
        }
        return brandRepository.searchBrands(keyword.trim(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "brands", key = "'allActive'")
    public List<Brand> getAllActiveBrands() {
        return brandRepository.findAllActive();
    }
}

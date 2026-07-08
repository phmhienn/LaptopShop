package com.laptopstore.application.facade;

import com.laptopstore.application.dto.product.BrandDTO;
import com.laptopstore.application.mapper.BrandMapper;
import com.laptopstore.business.service.BrandService;
import com.laptopstore.common.response.PagedResponse;
import com.laptopstore.data.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BrandFacade {

    private final BrandService brandService;
    private final BrandMapper brandMapper;

    public BrandDTO createBrand(BrandDTO requestDTO) {
        Brand brand = new Brand();
        brand.setName(requestDTO.getName());
        brand.setDescription(requestDTO.getDescription());
        brand.setLogo(requestDTO.getLogo());
        
        return brandMapper.toBrandDTO(brandService.createBrand(brand));
    }

    public BrandDTO updateBrand(Long id, BrandDTO requestDTO) {
        Brand brand = new Brand();
        brand.setName(requestDTO.getName());
        brand.setDescription(requestDTO.getDescription());
        brand.setLogo(requestDTO.getLogo());
        
        return brandMapper.toBrandDTO(brandService.updateBrand(id, brand));
    }

    public BrandDTO getBrandById(Long id) {
        return brandMapper.toBrandDTO(brandService.getBrandById(id));
    }

    public BrandDTO getBrandBySlug(String slug) {
        return brandMapper.toBrandDTO(brandService.getBrandBySlug(slug));
    }

    public void deleteBrand(Long id) {
        brandService.deleteBrand(id);
    }

    public void toggleBrandStatus(Long id) {
        brandService.toggleBrandStatus(id);
    }

    public List<BrandDTO> getAllActiveBrands() {
        return brandService.getAllActiveBrands().stream()
                .map(brandMapper::toBrandDTO)
                .collect(Collectors.toList());
    }

    public PagedResponse<BrandDTO> searchBrands(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Brand> brandsPage = brandService.searchBrands(keyword, pageable);

        List<BrandDTO> content = brandsPage.getContent().stream()
                .map(brandMapper::toBrandDTO)
                .collect(Collectors.toList());

        return PagedResponse.of(content, brandsPage.getNumber(), brandsPage.getSize(),
                brandsPage.getTotalElements(), brandsPage.getTotalPages());
    }
}

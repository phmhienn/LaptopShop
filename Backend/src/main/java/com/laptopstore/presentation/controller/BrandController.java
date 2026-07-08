package com.laptopstore.presentation.controller;

import com.laptopstore.application.dto.product.BrandDTO;
import com.laptopstore.application.facade.BrandFacade;
import com.laptopstore.common.constants.AppConstants;
import com.laptopstore.common.response.ApiResponse;
import com.laptopstore.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandFacade brandFacade;

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BrandDTO>>> getAllActiveBrands() {
        return ResponseEntity.ok(ApiResponse.success(brandFacade.getAllActiveBrands()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<BrandDTO>> getBrandBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(brandFacade.getBrandBySlug(slug)));
    }

    // Admin endpoints
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<BrandDTO>>> getAllBrands(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {
        
        PagedResponse<BrandDTO> response = brandFacade.searchBrands(keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BrandDTO>> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(brandFacade.getBrandById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BrandDTO>> createBrand(@RequestBody BrandDTO brandDTO) {
        BrandDTO createdBrand = brandFacade.createBrand(brandDTO);
        return ResponseEntity.ok(ApiResponse.success("Brand created successfully", createdBrand));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BrandDTO>> updateBrand(@PathVariable Long id, @RequestBody BrandDTO brandDTO) {
        BrandDTO updatedBrand = brandFacade.updateBrand(id, brandDTO);
        return ResponseEntity.ok(ApiResponse.success("Brand updated successfully", updatedBrand));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandFacade.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Brand deleted successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleBrandStatus(@PathVariable Long id) {
        brandFacade.toggleBrandStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Brand status toggled successfully"));
    }
}

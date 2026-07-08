package com.laptopstore.application.facade;

import com.laptopstore.application.dto.product.CategoryDTO;
import com.laptopstore.application.mapper.CategoryMapper;
import com.laptopstore.business.service.CategoryService;
import com.laptopstore.common.response.PagedResponse;
import com.laptopstore.data.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryFacade {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDTO createCategory(CategoryDTO requestDTO) {
        Category category = new Category();
        category.setName(requestDTO.getName());
        category.setDescription(requestDTO.getDescription());
        category.setImage(requestDTO.getImage());
        
        return categoryMapper.toCategoryDTOWithoutChildren(categoryService.createCategory(category, requestDTO.getParentId()));
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO requestDTO) {
        Category category = new Category();
        category.setName(requestDTO.getName());
        category.setDescription(requestDTO.getDescription());
        category.setImage(requestDTO.getImage());
        
        return categoryMapper.toCategoryDTOWithoutChildren(categoryService.updateCategory(id, category, requestDTO.getParentId()));
    }

    public CategoryDTO getCategoryById(Long id) {
        return categoryMapper.toCategoryDTOWithoutChildren(categoryService.getCategoryById(id));
    }

    public CategoryDTO getCategoryBySlug(String slug) {
        return categoryMapper.toCategoryDTO(categoryService.getCategoryBySlug(slug));
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryService.deleteCategory(id);
    }

    @Transactional
    public void toggleCategoryStatus(Long id) {
        categoryService.toggleCategoryStatus(id);
    }

    public List<CategoryDTO> getAllActiveRootCategories() {
        return categoryService.getAllActiveRootCategories().stream()
                .map(categoryMapper::toCategoryDTO)
                .collect(Collectors.toList());
    }

    public PagedResponse<CategoryDTO> searchCategories(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Category> categoriesPage = categoryService.searchCategories(keyword, pageable);

        List<CategoryDTO> content = categoriesPage.getContent().stream()
                .map(categoryMapper::toCategoryDTOWithoutChildren)
                .collect(Collectors.toList());

        return PagedResponse.of(content, categoriesPage.getNumber(), categoriesPage.getSize(),
                categoriesPage.getTotalElements(), categoriesPage.getTotalPages());
    }
}

package com.laptopstore.business.service.impl;

import com.laptopstore.business.exception.ResourceNotFoundException;
import com.laptopstore.business.exception.ValidationException;
import com.laptopstore.business.service.CategoryService;
import com.laptopstore.common.utils.SlugUtils;
import com.laptopstore.data.entity.Category;
import com.laptopstore.data.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(Category category, Long parentId) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new ValidationException("Category name already exists");
        }
        
        category.setSlug(SlugUtils.toSlug(category.getName()));
        
        if (categoryRepository.existsBySlug(category.getSlug())) {
            category.setSlug(category.getSlug() + "-" + System.currentTimeMillis());
        }
        
        if (parentId != null) {
            Category parent = getCategoryById(parentId);
            category.setParent(parent);
        }
        
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Category categoryDetails, Long parentId) {
        Category category = getCategoryById(id);
        
        if (!category.getName().equals(categoryDetails.getName()) && categoryRepository.existsByName(categoryDetails.getName())) {
            throw new ValidationException("Category name already exists");
        }
        
        category.setName(categoryDetails.getName());
        category.setSlug(SlugUtils.toSlug(categoryDetails.getName()));
        category.setDescription(categoryDetails.getDescription());
        
        if (categoryDetails.getImage() != null) {
            category.setImage(categoryDetails.getImage());
        }
        
        if (parentId != null) {
            if (parentId.equals(id)) {
                throw new ValidationException("A category cannot be its own parent");
            }
            Category parent = getCategoryById(parentId);
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
        
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        
        if (!category.getChildren().isEmpty()) {
            throw new ValidationException("Cannot delete category that has subcategories. Delete them first.");
        }
        
        if (!category.getProducts().isEmpty()) {
            throw new ValidationException("Cannot delete category that has products. Consider disabling it instead.");
        }
        
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public void toggleCategoryStatus(Long id) {
        Category category = getCategoryById(id);
        category.setStatus(!category.getStatus());
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Category> searchCategories(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return categoryRepository.findAll(pageable);
        }
        return categoryRepository.searchCategories(keyword.trim(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllActiveRootCategories() {
        return categoryRepository.findAllActiveRootCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAllActive();
    }
}

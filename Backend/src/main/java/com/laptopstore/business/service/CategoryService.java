package com.laptopstore.business.service;

import com.laptopstore.data.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    
    Category createCategory(Category category, Long parentId);
    
    Category updateCategory(Long id, Category categoryDetails, Long parentId);
    
    Category getCategoryById(Long id);
    
    Category getCategoryBySlug(String slug);
    
    void deleteCategory(Long id);
    
    void toggleCategoryStatus(Long id);
    
    Page<Category> searchCategories(String keyword, Pageable pageable);
    
    List<Category> getAllActiveRootCategories();
    
    List<Category> getAllActiveCategories();
}

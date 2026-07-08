package com.laptopstore.application.mapper;

import com.laptopstore.application.dto.product.CategoryDTO;
import com.laptopstore.data.entity.Category;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryDTO toCategoryDTO(Category category) {
        return toCategoryDTOWithChildren(category, true);
    }

    public CategoryDTO toCategoryDTOWithoutChildren(Category category) {
        return toCategoryDTOWithChildren(category, false);
    }

    private CategoryDTO toCategoryDTOWithChildren(Category category, boolean includeChildren) {
        if (category == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setImage(category.getImage());
        dto.setStatus(category.getStatus());

        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }

        if (includeChildren && category.getChildren() != null) {
            dto.setChildren(category.getChildren().stream()
                    .map(child -> toCategoryDTOWithChildren(child, true))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}

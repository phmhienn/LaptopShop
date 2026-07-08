package com.laptopstore.application.dto.product;

import lombok.Data;

import java.util.List;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String image;
    private Boolean status;
    private Long parentId;
    private List<CategoryDTO> children;
}

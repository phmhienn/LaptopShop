package com.laptopstore.data.repository;

import com.laptopstore.common.enums.ProductStatus;
import com.laptopstore.data.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> getSearchSpecification(
            String keyword, Long brandId, Long categoryId,
            BigDecimal minPrice, BigDecimal maxPrice,
            String ram, String cpu) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always only active products
            predicates.add(criteriaBuilder.equal(root.get("status"), ProductStatus.ACTIVE));

            if (StringUtils.hasText(keyword)) {
                String searchPattern = "%" + keyword.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern);
                Predicate descPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern);
                Predicate cpuPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("cpu")), searchPattern);
                Predicate gpuPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("gpu")), searchPattern);
                predicates.add(criteriaBuilder.or(namePredicate, descPredicate, cpuPredicate, gpuPredicate));
            }

            if (brandId != null) {
                predicates.add(criteriaBuilder.equal(root.join("brand").get("id"), brandId));
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.join("category").get("id"), categoryId));
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (StringUtils.hasText(ram)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("ram")), "%" + ram.toLowerCase() + "%"));
            }

            if (StringUtils.hasText(cpu)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("cpu")), "%" + cpu.toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

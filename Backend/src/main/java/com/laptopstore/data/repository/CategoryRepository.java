package com.laptopstore.data.repository;

import com.laptopstore.data.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    @Query("SELECT c FROM Category c WHERE c.status = true AND c.parent IS NULL ORDER BY c.name ASC")
    List<Category> findAllActiveRootCategories();

    @Query("SELECT c FROM Category c WHERE c.status = true ORDER BY c.name ASC")
    List<Category> findAllActive();

    List<Category> findByParentId(Long parentId);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Category> searchCategories(@Param("keyword") String keyword, Pageable pageable);
}

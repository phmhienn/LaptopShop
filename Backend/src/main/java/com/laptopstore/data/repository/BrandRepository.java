package com.laptopstore.data.repository;

import com.laptopstore.data.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    List<Brand> findByStatusTrue();

    @Query("SELECT b FROM Brand b WHERE b.status = true ORDER BY b.name ASC")
    List<Brand> findAllActive();

    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Brand> searchBrands(@Param("keyword") String keyword, Pageable pageable);
}

package com.laptopstore.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image", length = 500)
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // @JsonIgnore — tránh vòng lặp vô hạn khi Category chứa danh sách children lại chứa parent
    @JsonIgnore
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    @Column(name = "status")
    @Builder.Default
    private Boolean status = true;

    // @JsonIgnore — tránh serialize toàn bộ list products khi Category bị embed trong response
    @JsonIgnore
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

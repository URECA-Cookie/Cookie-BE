package com.cookie.admin.repository;

import com.cookie.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
        SELECT c
        FROM Category c
        WHERE c.subCategory = :category
    """)
    Optional<Category> findCategory(@Param("category") String category);
}

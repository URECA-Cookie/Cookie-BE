package com.cookie.domain.category.service;

import com.cookie.admin.repository.CategoryRepository;
import com.cookie.domain.category.entity.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("NOT_FOUND_CATEGORY"));
    }

}

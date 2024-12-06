package com.cookie.domain.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String mainCategory;
    private String subCategory;
}

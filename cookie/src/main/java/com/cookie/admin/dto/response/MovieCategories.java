package com.cookie.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieCategories {

    private Long categoryId;
    private String mainCategory;
    private String subCategory;
    private boolean isConnect;

    public MovieCategories(Long categoryId, String mainCategory, String subCategory, boolean isConnect) {
        this.categoryId = categoryId;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.isConnect = isConnect;
    }
}

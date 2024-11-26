package com.cookie.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieCategories {

    private Long categoryId;
    private String mainCategory;
    private String subCategory;
    private boolean isConnect;

    @JsonCreator
    public MovieCategories(
            @JsonProperty("categoryId") Long categoryId,
            @JsonProperty("mainCategory") String mainCategory,
            @JsonProperty("subCategory") String subCategory,
            @JsonProperty("isConnect") boolean isConnect) {
        this.categoryId = categoryId;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.isConnect = isConnect;
    }
}
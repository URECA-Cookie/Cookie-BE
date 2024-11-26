package com.cookie.domain.search.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequest {
    private String type;
    private String keyword;
    private int page = 0;
}

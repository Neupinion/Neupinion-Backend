package com.neupinion.neupinion.article.application.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NaverNewsResponse {

    private final String lastBuildDate;
    private final int total;
    private final int start;
    private final int display;
    private final List<NaverNewsItemResponse> items;
}

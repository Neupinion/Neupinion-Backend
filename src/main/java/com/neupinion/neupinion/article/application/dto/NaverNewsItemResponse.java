package com.neupinion.neupinion.article.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NaverNewsItemResponse {
    private final String title;
    private final String originallink;
    private final String description;
    private final String pubDate;
}

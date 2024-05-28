package com.neupinion.neupinion.article.application.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String url;
    private final LocalDateTime publishedAt;
    private final String stand;
    private final String reason;
}

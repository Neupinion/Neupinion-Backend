package com.neupinion.neupinion.article.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleSearchRequest {

    private final String searchKeyword;
    private final String issueDescription;
    private final String stands;
    private final String selectedStand;
}

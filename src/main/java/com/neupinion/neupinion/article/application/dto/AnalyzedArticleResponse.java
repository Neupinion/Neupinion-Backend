package com.neupinion.neupinion.article.application.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnalyzedArticleResponse {

    private final List<ArticleResponse> positiveArticles = new ArrayList<>();
    private final List<ArticleResponse> neutralArticles = new ArrayList<>();
    private final List<ArticleResponse> negativeArticles = new ArrayList<>();

    public static AnalyzedArticleResponse of(
        final List<ArticleResponse> positiveArticles,
        final List<ArticleResponse> neutralArticles,
        final List<ArticleResponse> negativeArticles
    ) {
        final AnalyzedArticleResponse response = new AnalyzedArticleResponse();
        response.positiveArticles.addAll(positiveArticles);
        response.neutralArticles.addAll(neutralArticles);
        response.negativeArticles.addAll(negativeArticles);

        return response;
    }
}

package com.neupinion.neupinion.article.ui;

import com.neupinion.neupinion.article.application.ArticleService;
import com.neupinion.neupinion.article.application.dto.AnalyzedArticleResponse;
import com.neupinion.neupinion.article.application.dto.ArticleSearchRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/issue/search")
    public ResponseEntity<AnalyzedArticleResponse> searchArticlesByIssue(
        @RequestBody @Valid final ArticleSearchRequest request
    ) {
        return ResponseEntity.ok(articleService.getArticles(request));
    }
}

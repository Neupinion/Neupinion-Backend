package com.neupinion.neupinion.article.ui;

import com.neupinion.neupinion.article.application.KeywordService;
import com.neupinion.neupinion.article.application.dto.KeywordCreateRequest;
import com.neupinion.neupinion.article.application.dto.KeywordResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/issue/{issueId}/keyword")
@RequiredArgsConstructor
@RestController
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping
    public ResponseEntity<KeywordResponse> getKeywords(
        @PathVariable final Long issueId
    ) {
        return ResponseEntity.ok(keywordService.getKeywords(issueId));
    }

    @PostMapping
    public ResponseEntity<Void> registerKeywords(
        @PathVariable final Long issueId,
        @RequestBody @Valid final KeywordCreateRequest request
    ) {
        keywordService.registerKeywords(request, issueId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

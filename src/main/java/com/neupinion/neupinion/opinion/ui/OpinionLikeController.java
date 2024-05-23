package com.neupinion.neupinion.opinion.ui;

import com.neupinion.neupinion.opinion.application.OpinionLikeService;
import com.neupinion.neupinion.opinion.application.dto.OpinionLikeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/reprocessed-issue/{reprocessedIssueId}/opinion/{opinionId}/like")
@RequiredArgsConstructor
@RestController
public class OpinionLikeController {

    private final OpinionLikeService opinionLikeService;

    @PutMapping
    public ResponseEntity<Void> likeOpinion(
        @PathVariable final Long reprocessedIssueId,
        @PathVariable final Long opinionId,
        @RequestBody @Valid final OpinionLikeRequest request
    ) {
        opinionLikeService.updateLike(opinionId, request, 1L); // TODO: 23/5/24 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.ok().build();
    }
}

package com.neupinion.neupinion.opinion.ui;

import com.neupinion.neupinion.auth.ui.argumentresolver.Authenticated;
import com.neupinion.neupinion.auth.ui.argumentresolver.MemberInfo;
import com.neupinion.neupinion.opinion.application.OpinionLikeService;
import com.neupinion.neupinion.opinion.application.dto.OpinionLikeRequest;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @RequestBody @Valid final OpinionLikeRequest request,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        opinionLikeService.updateLike(opinionId, request, memberInfo.memberId());

        return ResponseEntity.ok().build();
    }
}

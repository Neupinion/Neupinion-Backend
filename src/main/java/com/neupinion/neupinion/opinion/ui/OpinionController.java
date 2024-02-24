package com.neupinion.neupinion.opinion.ui;

import com.neupinion.neupinion.opinion.application.OpinionService;
import com.neupinion.neupinion.opinion.application.dto.FollowUpIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionCreateRequest;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class OpinionController {

    private final OpinionService opinionService;

    @PostMapping("/follow-up-issue/opinion")
    public ResponseEntity<Void> createFollowUpIssueOpinion(
        final Long memberId,
        @Valid @RequestBody final FollowUpIssueOpinionCreateRequest request
    ) {
        final Long opinionId = opinionService.createFollowUpIssueOpinion(1L,
                                                                         request); // TODO: 2/24/24 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.created(URI.create("opinion/" + opinionId)).build();
    }

    @PostMapping("/reprocessed-issue/opinion")
    public ResponseEntity<Void> createReprocessedIssueOpinion(
        final Long memberId,
        @Valid @RequestBody final ReprocessedIssueOpinionCreateRequest request
    ) {
        final Long opinionId = opinionService.createReprocessedIssueOpinion(1L,
                                                                            request); // TODO: 2/24/24 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.created(URI.create("opinion/" + opinionId)).build();
    }
}

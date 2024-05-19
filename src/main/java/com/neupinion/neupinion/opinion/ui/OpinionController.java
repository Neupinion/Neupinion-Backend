package com.neupinion.neupinion.opinion.ui;

import com.neupinion.neupinion.opinion.application.OpinionService;
import com.neupinion.neupinion.opinion.application.dto.FollowUpIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.MyOpinionResponse;
import com.neupinion.neupinion.opinion.application.dto.OpinionParagraphResponse;
import com.neupinion.neupinion.opinion.application.dto.OpinionUpdateRequest;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionResponse;
import com.neupinion.neupinion.query_mode.order.OrderMode;
import com.neupinion.neupinion.query_mode.view.opinion.OpinionViewMode;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class OpinionController {

    private final OpinionService opinionService;

    @PostMapping("/follow-up-issue/opinion")
    public ResponseEntity<Void> createFollowUpIssueOpinion(
        @Valid @RequestBody final FollowUpIssueOpinionCreateRequest request
    ) {
        final Long opinionId = opinionService.createFollowUpIssueOpinion(1L,
                                                                         request); // TODO: 2/24/24 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.created(URI.create("/follow-up-issue/opinion/" + opinionId)).build();
    }

    @PostMapping("/reprocessed-issue/opinion")
    public ResponseEntity<Void> createReprocessedIssueOpinion(
        @Valid @RequestBody final ReprocessedIssueOpinionCreateRequest request
    ) {
        final Long opinionId = opinionService.createReprocessedIssueOpinion(1L,
                                                                            request); // TODO: 2/24/24 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.created(URI.create("/reprocessed-issue/opinion/" + opinionId)).build();
    }

    @PatchMapping("/reprocessed-issue/opinion/{opinionId}")
    public ResponseEntity<Void> updateReprocessedIssueOpinion(
        @PathVariable final Long opinionId,
        @Valid @RequestBody final OpinionUpdateRequest request
    ) {
        opinionService.updateReprocessedIssueOpinion(1L, opinionId, request); // TODO: 2/24/24 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/follow-up-issue/opinion/{opinionId}")
    public ResponseEntity<Void> updateFollowUpIssueOpinion(
        @PathVariable final Long opinionId,
        @Valid @RequestBody final OpinionUpdateRequest request
    ) {
        opinionService.updateFollowUpIssueOpinion(1L, opinionId, request); // TODO: 2/24/24 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/follow-up-issue/{issueId}/me")
    public ResponseEntity<List<MyOpinionResponse>> getMyFollowUpIssueOpinions(
        @PathVariable final Long issueId
    ) {
        final List<MyOpinionResponse> responses = opinionService.getMyFollowUpOpinions(1L,
                                                                                       issueId); // TODO: 2/24/24 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/reprocessed-issue/{issueId}/me")
    public ResponseEntity<List<MyOpinionResponse>> getMyReprocessedIssueOpinions(
        @PathVariable final Long issueId
    ) {
        final List<MyOpinionResponse> responses = opinionService.getMyReprocessedOpinions(1L,
                                                                                          issueId); // TODO: 2/24/24 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/follow-up-issue/opinion/{opinionId}")
    public ResponseEntity<Void> deleteFollowUpIssueOpinion(
        @PathVariable final Long opinionId
    ) {
        opinionService.deleteFollowUpIssueOpinion(1L, opinionId); // TODO: 2/24/24 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reprocessed-issue/opinion/{opinionId}")
    public ResponseEntity<Void> deleteReprocessedIssueOpinion(
        @PathVariable final Long opinionId
    ) {
        opinionService.deleteReprocessedIssueOpinion(1L, opinionId); // TODO: 2/24/24 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reprocessed-issue/{issueId}/opinion")
    public ResponseEntity<List<ReprocessedIssueOpinionResponse>> getReprocessedIssueOpinions(
        @PathVariable final Long issueId,
        @RequestParam(name = "viewMode", required = false, defaultValue = "ALL") final String viewMode,
        @RequestParam(name = "orderMode", required = false, defaultValue = "RECENT") final String orderMode,
        @RequestParam(name = "page", required = false, defaultValue = "0") final Integer page
    ) {
        final OpinionViewMode filter = OpinionViewMode.from(viewMode);
        final OrderMode orderFilter = OrderMode.from(orderMode);

        return ResponseEntity.ok(opinionService.getReprocessedIssueOpinions(issueId, 1L, filter,
                                                                            orderFilter,
                                                                            page)); // TODO: 2/24/24 추후 액세스 토큰 인증 로직 추가하기
    }

    @GetMapping("/reprocessed-issue/{issueId}/opinion/top")
    public ResponseEntity<List<ReprocessedIssueOpinionResponse>> getTopReprocessedIssueOpinions(
        @PathVariable final Long issueId
    ) {
        final List<ReprocessedIssueOpinionResponse> responses = opinionService.getTopReprocessedIssueOpinions(issueId,
                                                                                                              1L);  // TODO: 24. 4. 20. 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/reprocessed-issue/{issueId}/opinion/paragraph")
    public ResponseEntity<List<OpinionParagraphResponse>> getReprocessedIssueOpinionsOrderByParagraph(
        @PathVariable final Long issueId,
        @RequestParam(name = "viewMode", required = false, defaultValue = "ALL") final String viewMode,
        @RequestParam(name = "orderMode", required = false, defaultValue = "RECENT") final String orderMode,
        @RequestParam(required = false, defaultValue = "0") final Integer page
    ) {
        final OpinionViewMode filter = OpinionViewMode.from(viewMode);
        final OrderMode orderFilter = OrderMode.from(orderMode);
        final List<OpinionParagraphResponse> responses = opinionService.getReprocessedIssueOpinionsOrderByParagraph(
            issueId, 1L, orderFilter, filter, page);

        return ResponseEntity.ok(responses);
    }
}

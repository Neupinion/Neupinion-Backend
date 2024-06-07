package com.neupinion.neupinion.opinion.ui;

import com.neupinion.neupinion.auth.ui.argumentresolver.Authenticated;
import com.neupinion.neupinion.auth.ui.argumentresolver.MemberInfo;
import com.neupinion.neupinion.opinion.application.OpinionService;
import com.neupinion.neupinion.opinion.application.dto.FollowUpIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.MyOpinionResponse;
import com.neupinion.neupinion.opinion.application.dto.OpinionParagraphResponse;
import com.neupinion.neupinion.opinion.application.dto.OpinionUpdateRequest;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionResponse;
import com.neupinion.neupinion.query_mode.order.OrderMode;
import com.neupinion.neupinion.query_mode.view.opinion.OpinionViewMode;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @Valid @RequestBody final FollowUpIssueOpinionCreateRequest request,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        final Long opinionId = opinionService.createFollowUpIssueOpinion(memberInfo.memberId(),
                                                                         request);

        return ResponseEntity.created(URI.create("/follow-up-issue/opinion/" + opinionId)).build();
    }

    @PostMapping("/reprocessed-issue/opinion")
    public ResponseEntity<Void> createReprocessedIssueOpinion(
        @Valid @RequestBody final ReprocessedIssueOpinionCreateRequest request,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        final Long opinionId = opinionService.createReprocessedIssueOpinion(memberInfo.memberId(), request);

        return ResponseEntity.created(URI.create("/reprocessed-issue/opinion/" + opinionId)).build();
    }

    @PatchMapping("/reprocessed-issue/opinion/{opinionId}")
    public ResponseEntity<Void> updateReprocessedIssueOpinion(
        @PathVariable final Long opinionId,
        @Valid @RequestBody final OpinionUpdateRequest request,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        opinionService.updateReprocessedIssueOpinion(memberInfo.memberId(), opinionId, request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/follow-up-issue/opinion/{opinionId}")
    public ResponseEntity<Void> updateFollowUpIssueOpinion(
        @PathVariable final Long opinionId,
        @Valid @RequestBody final OpinionUpdateRequest request,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        opinionService.updateFollowUpIssueOpinion(memberInfo.memberId(), opinionId, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/follow-up-issue/{issueId}/me")
    public ResponseEntity<List<MyOpinionResponse>> getMyFollowUpIssueOpinions(
        @PathVariable final Long issueId,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        final List<MyOpinionResponse> responses = opinionService.getMyFollowUpOpinions(memberInfo.memberId(),
                                                                                       issueId);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/reprocessed-issue/{issueId}/me")
    public ResponseEntity<List<MyOpinionResponse>> getMyReprocessedIssueOpinions(
        @PathVariable final Long issueId,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        final List<MyOpinionResponse> responses = opinionService.getMyReprocessedOpinions(memberInfo.memberId(), issueId);

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/follow-up-issue/opinion/{opinionId}")
    public ResponseEntity<Void> deleteFollowUpIssueOpinion(
        @PathVariable final Long opinionId,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        opinionService.deleteFollowUpIssueOpinion(memberInfo.memberId(), opinionId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reprocessed-issue/opinion/{opinionId}")
    public ResponseEntity<Void> deleteReprocessedIssueOpinion(
        @PathVariable final Long opinionId,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        opinionService.deleteReprocessedIssueOpinion(memberInfo.memberId(), opinionId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reprocessed-issue/{issueId}/opinion")
    public ResponseEntity<List<ReprocessedIssueOpinionResponse>> getReprocessedIssueOpinions(
        @PathVariable final Long issueId,
        @RequestParam(name = "viewMode", required = false, defaultValue = "ALL") final String viewMode,
        @RequestParam(name = "orderMode", required = false, defaultValue = "RECENT") final String orderMode,
        @RequestParam(name = "page", required = false, defaultValue = "0") final Integer page,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        final OpinionViewMode filter = OpinionViewMode.from(viewMode);
        final OrderMode orderFilter = OrderMode.from(orderMode);

        return ResponseEntity.ok(
            opinionService.getReprocessedIssueOpinions(issueId, memberInfo.memberId(), filter, orderFilter, page));
    }

    @GetMapping("/reprocessed-issue/{issueId}/opinion/top")
    public ResponseEntity<List<ReprocessedIssueOpinionResponse>> getTopReprocessedIssueOpinions(
        @PathVariable final Long issueId,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        final List<ReprocessedIssueOpinionResponse> responses = opinionService.getTopReprocessedIssueOpinions(issueId,
                                                                                                              memberInfo.memberId());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/reprocessed-issue/{issueId}/opinion/paragraph")
    public ResponseEntity<List<OpinionParagraphResponse>> getReprocessedIssueOpinionsOrderByParagraph(
        @PathVariable final Long issueId,
        @RequestParam(name = "viewMode", required = false, defaultValue = "ALL") final String viewMode,
        @RequestParam(name = "orderMode", required = false, defaultValue = "RECENT") final String orderMode,
        @RequestParam(required = false, defaultValue = "0") final Integer page,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        final OpinionViewMode filter = OpinionViewMode.from(viewMode);
        final OrderMode orderFilter = OrderMode.from(orderMode);
        final List<OpinionParagraphResponse> responses = opinionService.getReprocessedIssueOpinionsOrderByParagraph(
            issueId, memberInfo.memberId(), orderFilter, filter, page);

        return ResponseEntity.ok(responses);
    }
}

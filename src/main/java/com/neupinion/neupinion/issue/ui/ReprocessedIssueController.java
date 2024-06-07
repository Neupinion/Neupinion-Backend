package com.neupinion.neupinion.issue.ui;

import com.neupinion.neupinion.auth.ui.argumentresolver.Authenticated;
import com.neupinion.neupinion.auth.ui.argumentresolver.MemberInfo;
import com.neupinion.neupinion.issue.application.ReprocessedIssueService;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssuesByReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.RecentReprocessedIssueByCategoryResponse;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueVoteResultResponse;
import com.neupinion.neupinion.issue.application.dto.ShortReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.TrustVoteRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/reprocessed-issue")
@RestController
public class ReprocessedIssueController {

    private final ReprocessedIssueService reprocessedIssueService;

    @PostMapping
    public ResponseEntity<Void> save(
        @Valid @RequestBody final ReprocessedIssueCreateRequest request
    ) {
        final Long savedReprocessedIssueId = reprocessedIssueService.createReprocessedIssue(request);

        return ResponseEntity.created(URI.create("/reprocessed-issue/" + savedReprocessedIssueId)).build();
    }

    @GetMapping
    public ResponseEntity<List<ShortReprocessedIssueResponse>> findReprocessedIssueResponses(
        @RequestParam(value = "date") final String rawDate) {
        final List<ShortReprocessedIssueResponse> response = reprocessedIssueService.findReprocessedIssues(rawDate);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReprocessedIssueResponse> findReprocessedIssueResponse(
        @PathVariable final Long id,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        final ReprocessedIssueResponse response = reprocessedIssueService.findReprocessedIssue(memberInfo.memberId(),
                                                                                               id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/trust-vote")
    public ResponseEntity<Void> voteTrust(
        @PathVariable final Long id,
        @RequestBody final TrustVoteRequest vote,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        reprocessedIssueService.vote(memberInfo.memberId(), id, vote);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<RecentReprocessedIssueByCategoryResponse>> findReprocessedIssueResponsesByCategory(
        @RequestParam(value = "current") final Long id,
        @RequestParam(value = "category") final String category
    ) {
        final List<RecentReprocessedIssueByCategoryResponse> responses = reprocessedIssueService.findReprocessedIssuesByCategory(
            id, category);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/trust-vote")
    public ResponseEntity<ReprocessedIssueVoteResultResponse> getVoteResult(@PathVariable final Long id) {
        final ReprocessedIssueVoteResultResponse response = reprocessedIssueService.getVoteResult(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/follow-up-issue")
    public ResponseEntity<FollowUpIssuesByReprocessedIssueResponse> getFollowUpIssues(@PathVariable final Long id) {
        FollowUpIssuesByReprocessedIssueResponse response = reprocessedIssueService.getFollowUpIssues(id);

        return ResponseEntity.ok(response);
    }
}

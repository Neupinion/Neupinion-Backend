package com.neupinion.neupinion.issue.ui;

import com.neupinion.neupinion.auth.ui.argumentresolver.Authenticated;
import com.neupinion.neupinion.auth.ui.argumentresolver.MemberInfo;
import com.neupinion.neupinion.issue.application.FollowUpIssueService;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueByCategoryResponse;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueOfVotedReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueResponse;
import com.neupinion.neupinion.query_mode.view.follow_up_issue.FollowUpIssueViewStrategy;
import com.neupinion.neupinion.query_mode.view.follow_up_issue.ViewMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(value = "/follow-up-issue")
@RestController
public class FollowUpIssueController {

    private final Map<ViewMode, FollowUpIssueViewStrategy> strategies;
    private final FollowUpIssueService followUpIssueService;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody final FollowUpIssueCreateRequest request) {
        final Long followUpIssueId = followUpIssueService.createFollowUpIssue(request);

        return ResponseEntity.created(URI.create("/follow-up-issue/" + followUpIssueId)).build();
    }

    @GetMapping
    public ResponseEntity<List<FollowUpIssueByCategoryResponse>> getByCategoryAndDate(
        @RequestParam(name = "category") final String category,
        @RequestParam(name = "date") final String dateFormat,
        @RequestParam(name = "viewMode", required = false, defaultValue = "ALL") final String viewMode,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
        ) {
        final ViewMode filter = ViewMode.from(viewMode);
        final FollowUpIssueViewStrategy strategy = strategies.getOrDefault(filter, strategies.get(ViewMode.ALL));

        return ResponseEntity.ok(
            strategy.findIssueByCategoryAndDate(dateFormat, category, memberInfo.memberId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FollowUpIssueResponse> getById(
        @PathVariable final Long id,
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        return ResponseEntity.ok(followUpIssueService.findById(id, memberInfo.memberId()));
    }

    @GetMapping("/unviewed")
    public ResponseEntity<List<FollowUpIssueOfVotedReprocessedIssueResponse>> getUnviewedSortByLatest(
        @Authenticated @Schema(hidden = true) final MemberInfo memberInfo
    ) {
        return ResponseEntity.ok(
            followUpIssueService.findFollowUpIssuesOfVotedReprocessedIssue(memberInfo.memberId()));
    }
}

package com.neupinion.neupinion.issue.ui;

import com.neupinion.neupinion.issue.application.FollowUpIssueService;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueByCategoryResponse;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueCreateRequest;
import com.neupinion.neupinion.issue.application.viewmode.FollowUpIssueViewStrategy;
import com.neupinion.neupinion.issue.application.viewmode.ViewMode;
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

    private final Map<String, FollowUpIssueViewStrategy> strategies;
    private final FollowUpIssueService followUpIssueService;

    @PostMapping
    public ResponseEntity<Void> createFollowUpIssue(@Valid @RequestBody final FollowUpIssueCreateRequest request) {
        final Long followUpIssueId = followUpIssueService.createFollowUpIssue(request);

        return ResponseEntity.created(URI.create("/follow-up-issue/" + followUpIssueId)).build();
    }

    @GetMapping
    public ResponseEntity<List<FollowUpIssueByCategoryResponse>> getMyFollowUpIssueByCategoryAndDate(
        @RequestParam(name = "category") final String category,
        @RequestParam(name = "date") final String dateFormat,
        @RequestParam(name = "viewMode", required = false, defaultValue = "ALL") final String viewMode
    ) {
        final ViewMode filter = ViewMode.from(viewMode);
        final FollowUpIssueViewStrategy strategy = strategies.getOrDefault(filter.name(),
                                                                           strategies.get(ViewMode.ALL.name()));

        return ResponseEntity.ok(
            strategy.findIssueByCategoryAndDate(dateFormat, category, 1L)); // TODO: 추후 액세스 토큰 인증 로직 추가하기
    }
}

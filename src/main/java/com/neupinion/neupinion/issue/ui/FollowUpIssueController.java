package com.neupinion.neupinion.issue.ui;

import com.neupinion.neupinion.issue.application.FollowUpIssueService;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueByCategoryResponse;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueCreateRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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

    private final FollowUpIssueService followUpIssueService;

    @PostMapping
    public ResponseEntity<Void> createFollowUpIssue(@Valid @RequestBody final FollowUpIssueCreateRequest request) {
        final Long followUpIssueId = followUpIssueService.createFollowUpIssue(request);

        return ResponseEntity.created(URI.create("/follow-up-issue/" + followUpIssueId)).build();
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<FollowUpIssueByCategoryResponse>> getFollowUpIssueByCategoryAndDate(
        @PathVariable final String category,
        @RequestParam final String dateFormat
    ) {
        final List<FollowUpIssueByCategoryResponse> followUpIssues = followUpIssueService.findFollowUpIssueByCategoryAndDate(
            dateFormat, category, 1L); // TODO: 추후 액세스 토큰 인증 로직 추가하기

        return ResponseEntity.ok(followUpIssues);
    }
}

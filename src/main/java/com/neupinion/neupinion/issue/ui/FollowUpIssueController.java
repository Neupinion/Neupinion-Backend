package com.neupinion.neupinion.issue.ui;

import com.neupinion.neupinion.issue.application.FollowUpIssueService;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueCreateRequest;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}

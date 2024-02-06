package com.neupinion.neupinion.issue.ui;

import com.neupinion.neupinion.issue.application.ReprocessedIssueService;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<List<ReprocessedIssueResponse>> findReprocessedIssueResponses(
        @RequestParam(value = "date") final String rawDate) {
        final List<ReprocessedIssueResponse> response = reprocessedIssueService.findReprocessedIssues(rawDate);

        return ResponseEntity.ok(response);
    }
}

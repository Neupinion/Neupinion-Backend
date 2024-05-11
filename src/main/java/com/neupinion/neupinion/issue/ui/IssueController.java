package com.neupinion.neupinion.issue.ui;

import com.neupinion.neupinion.issue.application.IssueService;
import com.neupinion.neupinion.issue.application.dto.IntegratedVoteResultResponse;
import com.neupinion.neupinion.issue.application.dto.TimelineResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/issue")
@RestController
public class IssueController {

    private final IssueService issueService;

    @GetMapping("/{issueId}/integrated-result")
    public ResponseEntity<IntegratedVoteResultResponse> getIntegratedVoteResult(@PathVariable final Long issueId) {
        return ResponseEntity.ok(issueService.getIntegratedVoteResult(issueId));
    }

    @GetMapping("/{issueId}/time-line")
    public ResponseEntity<List<TimelineResponse>> getIssueTimeLine(@PathVariable final Long issueId) {
        return ResponseEntity.ok(issueService.getIssueTimeLine(issueId));
    }
}

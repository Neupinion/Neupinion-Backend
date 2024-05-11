package com.neupinion.neupinion.issue.ui;

import com.neupinion.neupinion.issue.application.IssueService;
import com.neupinion.neupinion.issue.application.dto.IntegratedVoteResultResponse;
import lombok.RequiredArgsConstructor;
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
    public IntegratedVoteResultResponse getIntegratedVoteResult(@PathVariable final Long issueId) {
        return issueService.getIntegratedVoteResult(issueId);
    }
}

package com.neupinion.neupinion.issue.ui;

import com.neupinion.neupinion.issue.application.RelatedIssueService;
import com.neupinion.neupinion.issue.application.dto.RelatedIssueResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RelatedIssueController {

    private final RelatedIssueService relatedIssueService;

    @GetMapping("/reprocessed-issue/{id}/related-issue")
    public ResponseEntity<List<RelatedIssueResponse>> getRelatedIssues(@PathVariable final Long id) {
        final List<RelatedIssueResponse> responses = relatedIssueService.getRelatedIssues(id);

        return ResponseEntity.ok(responses);
    }
}

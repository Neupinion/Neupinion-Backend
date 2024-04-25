package com.neupinion.neupinion.issue.application;

import com.neupinion.neupinion.issue.application.dto.RelatedIssueResponse;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RelatedIssueService {

    private static final int RELATED_ISSUE_LIMIT = 3;

    private final ReprocessedIssueRepository reprocessedIssueRepository;
    private final FollowUpIssueRepository followUpIssueRepository;

    public List<RelatedIssueResponse> getRelatedIssues(final Long id, final Random random) {
        final int reprocessedIssueCount = random.nextInt(RELATED_ISSUE_LIMIT - 1) + 1;
        final int followUpIssueCount = RELATED_ISSUE_LIMIT - reprocessedIssueCount;
        final List<ReprocessedIssue> reprocessedIssues = reprocessedIssueRepository.findRandomReprocessedIssuesExceptId(
            id, PageRequest.of(0, reprocessedIssueCount));
        final List<FollowUpIssue> followUpIssues = followUpIssueRepository.findRandomFollowUpIssuesExceptReprocessedIssueId(
            id, PageRequest.of(0, followUpIssueCount));

        List<RelatedIssueResponse> responses = new ArrayList<>();
        responses.addAll(reprocessedIssues.stream()
                             .map(RelatedIssueResponse::from)
                             .toList());
        responses.addAll(followUpIssues.stream()
                             .map(RelatedIssueResponse::from)
                             .toList());

        return responses;
    }
}

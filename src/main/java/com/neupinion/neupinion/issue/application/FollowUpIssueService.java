package com.neupinion.neupinion.issue.application;

import com.neupinion.neupinion.issue.application.dto.FollowUpIssueCreateRequest;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.exception.ReprocessedIssueException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FollowUpIssueService {

    private final ReprocessedIssueRepository reprocessedIssueRepository;
    private final FollowUpIssueRepository followUpIssueRepository;

    @Transactional
    public Long createFollowUpIssue(final FollowUpIssueCreateRequest request) {
        validateReprocessedIssue(request.getReprocessedIssueId());
        final FollowUpIssue savedFollowUpIssue = followUpIssueRepository.save(request.toEntity());

        return savedFollowUpIssue.getId();
    }

    private void validateReprocessedIssue(final Long reprocessedIssueId) {
        reprocessedIssueRepository.findById(reprocessedIssueId)
            .orElseThrow(ReprocessedIssueException.ReprocessedIssueNotFoundException::new);
    }
}

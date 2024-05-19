package com.neupinion.neupinion.query_mode.order;

import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.opinion.domain.repository.dto.IssueOpinionMapping;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class PopularAllOpinionOrderStrategy implements AllOpinionOrderStrategy {

    private final ReprocessedIssueRepository reprocessedIssueRepository;
    private final FollowUpIssueRepository followUpIssueRepository;

    @Override
    public List<IssueOpinionMapping> getOpinionsByReliabilitiesOrderBy(final Long issueId,
                                                                       final List<Boolean> reliabilities,
                                                                       final Pageable pageable) {
        final List<Long> followUpIssueIds = followUpIssueRepository.findByReprocessedIssueId(issueId).stream()
            .map(FollowUpIssue::getId)
            .toList();

        return reprocessedIssueRepository.findAllCommentsOrderByLikesAtDesc(issueId, followUpIssueIds, reliabilities,
                                                                            pageable);
    }
}

package com.neupinion.neupinion.query_mode.order;

import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PopularOpinionOrderStrategy implements OpinionOrderStrategy {

    private final ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;

    @Override
    public List<ReprocessedIssueOpinion> getOpinionsByReliabilitiesOrderBy(final Long issueId,
                                                                           final List<Boolean> reliabilities) {
        return reprocessedIssueOpinionRepository.findByIssueIdAndIsReliableWithLikes(issueId, reliabilities);
    }

    @Override
    public List<ReprocessedIssueOpinion> getOpinionsByParagraphOrderBy(final Long paragraphId,
                                                                       final List<Boolean> reliabilities) {
        return reprocessedIssueOpinionRepository.findTop5ByParagraphIdOrderByLikes(paragraphId, reliabilities);
    }
}

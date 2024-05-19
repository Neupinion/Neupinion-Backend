package com.neupinion.neupinion.query_mode.order;

import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class PopularOpinionOrderStrategy implements OpinionOrderStrategy {

    private final ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;

    @Override
    public List<ReprocessedIssueOpinion> getOpinionsByReliabilitiesOrderBy(final Long issueId,
                                                                           final List<Boolean> reliabilities,
                                                                           final Pageable pageable) {
        return reprocessedIssueOpinionRepository.findByIssueIdAndIsReliableWithLikes(issueId, reliabilities, pageable);
    }

    @Override
    public List<ReprocessedIssueOpinion> getOpinionsByParagraphOrderBy(final Long paragraphId,
                                                                       final List<Boolean> reliabilities,
                                                                       final Pageable pageable) {
        return reprocessedIssueOpinionRepository.findTop5ByParagraphIdOrderByLikes(paragraphId, reliabilities,
                                                                                   pageable);
    }
}

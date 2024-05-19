package com.neupinion.neupinion.query_mode.order;

import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OpinionOrderStrategy {

    List<ReprocessedIssueOpinion> getOpinionsByParagraphOrderBy(final Long paragraphId,
                                                                final List<Boolean> reliabilities,
                                                                final Pageable pageable);

    List<ReprocessedIssueOpinion> getOpinionsByReliabilitiesOrderBy(final Long issueId,
                                                                    final List<Boolean> reliabilities,
                                                                    final Pageable pageable);
}

package com.neupinion.neupinion.query_mode.order;

import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import java.util.List;

public interface OpinionOrderStrategy {

    List<ReprocessedIssueOpinion> getOpinionsByParagraphOrderBy(final Long paragraphId,
                                                                final List<Boolean> reliabilities);

    List<ReprocessedIssueOpinion> getOpinionsByReliabilitiesOrderBy(final Long issueId, final List<Boolean> reliabilities);
}

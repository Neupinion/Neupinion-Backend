package com.neupinion.neupinion.query_mode.order;

import com.neupinion.neupinion.opinion.domain.repository.dto.IssueOpinionMapping;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface AllOpinionOrderStrategy {

    List<IssueOpinionMapping> getOpinionsByReliabilitiesOrderBy(final Long issueId,
                                                                final List<Boolean> reliabilities,
                                                                final Pageable pageable);
}

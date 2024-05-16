package com.neupinion.neupinion.query_mode.view.opinion;

import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionResponse;
import java.util.List;

public interface OpinionViewStrategy {

    List<ReprocessedIssueOpinionResponse> getOpinionsByReliable(final OpinionViewMode viewMode, final Long issueId, final Long memberId);
}

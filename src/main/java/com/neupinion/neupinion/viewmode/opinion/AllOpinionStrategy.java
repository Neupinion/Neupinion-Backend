package com.neupinion.neupinion.viewmode.opinion;

import com.neupinion.neupinion.opinion.application.OpinionService;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AllOpinionStrategy implements OpinionViewStrategy {

    private final OpinionService opinionService;

    @Override
    public List<ReprocessedIssueOpinionResponse> getOpinionsByReliable(final OpinionViewMode viewMode, final Long issueId,
                                                                       final Long memberId) {
        return opinionService.getReprocessedIssueOpinions(issueId, memberId);
    }
}

package com.neupinion.neupinion.viewmode.opinion;

import com.neupinion.neupinion.opinion.application.OpinionService;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReliableOpinionStrategy implements OpinionViewStrategy {

    private final OpinionService opinionService;

    @Override
    public List<ReprocessedIssueOpinionResponse> getOpinionsByReliable(final OpinionViewMode viewMode,
                                                                       final Long issueId, final Long memberId) {
        final boolean isReliable = viewMode == OpinionViewMode.TRUST;

        return opinionService.getOpinionsByReliable(isReliable, issueId, memberId);
    }
}

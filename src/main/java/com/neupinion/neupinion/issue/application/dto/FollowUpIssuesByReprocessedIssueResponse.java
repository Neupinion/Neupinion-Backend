package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "재가공 이슈의 후속 이슈 정보 응답")
@AllArgsConstructor
@Getter
public class FollowUpIssuesByReprocessedIssueResponse {

    @Schema(description = "재가공 이슈 제목", example = "재가공 이슈 제목")
    private final String title;

    @Schema(description = "발행 일자 오름차순으로 정렬된 후속 이슈 3개")
    private final List<ShortFollowUpIssueResponse> followUpIssues;

    @Schema(description = "통합 투표를 볼 수 있는지 여부", example = "true")
    private final boolean isIntegratedVotePossible;

    public static FollowUpIssuesByReprocessedIssueResponse of(final ReprocessedIssue reprocessedIssue,
                                                              final List<FollowUpIssue> followUpIssues,
                                                              final boolean isIntegratedVotePossible) {
        final List<ShortFollowUpIssueResponse> followUpIssueResponses = followUpIssues.stream()
            .map(ShortFollowUpIssueResponse::from)
            .toList();

        return new FollowUpIssuesByReprocessedIssueResponse(reprocessedIssue.getTitle(), followUpIssueResponses, isIntegratedVotePossible);
    }

    public boolean getIsIntegratedVotePossible() {
        return isIntegratedVotePossible;
    }
}

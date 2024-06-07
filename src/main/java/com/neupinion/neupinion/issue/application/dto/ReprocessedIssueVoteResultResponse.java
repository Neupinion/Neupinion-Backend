package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.IssueStand;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "재가공 이슈 개별 투표 결과")
@AllArgsConstructor
@Getter
public class ReprocessedIssueVoteResultResponse {

    @Schema(description = "총 투표 수", example = "7000")
    private final int totalVoteCount;

    @Schema(description = "가장 많은 투표 입장", example = "민희진")
    private final String mostVotedStand;

    @Schema(description = "가장 많은 투표 결과 값의 투표 수", example = "5342")
    private final int mostVotedCount;

    @Schema(description = "투표 순위 리스트")
    private final List<VoteRankingResponse> voteRankings;

    public static ReprocessedIssueVoteResultResponse of(final Map<IssueStand, Integer> percentages, final int mostVotedCount, final int totalVoteCount) {
        final List<VoteRankingResponse> voteRankings = percentages.entrySet().stream()
            .map(entry -> new VoteRankingResponse(entry.getKey().getStand(), entry.getValue()))
            .sorted((o1, o2) -> Integer.compare(o2.getRelatablePercentage(), o1.getRelatablePercentage()))
            .toList();

        return new ReprocessedIssueVoteResultResponse(totalVoteCount, voteRankings.get(0).getStand(), mostVotedCount, voteRankings);
    }
}

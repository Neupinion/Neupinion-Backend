package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.VoteStatus;
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

    @Schema(description = "가장 많은 투표 결과 값", example = "완전 의심")
    private final String mostVotedStatus;

    @Schema(description = "가장 많은 투표 결과 값의 투표 수", example = "5342")
    private final int mostVotedCount;

    @Schema(description = "투표 순위 리스트")
    private final List<VoteRankingResponse> voteRankings;

    public static ReprocessedIssueVoteResultResponse of(final Map<VoteStatus, Integer> votesCount,
                                                        final Map<VoteStatus, Integer> percentages) {
        final int totalVoteCount = votesCount.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
        final List<VoteRankingResponse> voteRankings = percentages.entrySet().stream()
            .map(entry -> new VoteRankingResponse(entry.getKey().getValue(), entry.getValue()))
            .sorted((o1, o2) -> Integer.compare(o2.getVotePercentage(), o1.getVotePercentage()))
            .toList();
        final VoteStatus mostVotedStatus = votesCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(VoteStatus.NOT_VOTED);
        final int mostVotedCount = votesCount.getOrDefault(mostVotedStatus, 0);

        return new ReprocessedIssueVoteResultResponse(totalVoteCount, mostVotedStatus.getValue(), mostVotedCount,
                                                      voteRankings);
    }
}

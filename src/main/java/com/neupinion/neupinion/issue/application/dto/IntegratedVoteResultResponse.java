package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.IssueStand;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "통합 투표 결과 응답")
@AllArgsConstructor
@Getter
public class IntegratedVoteResultResponse {

    @Schema(description = "가장 많은 투표를 받은 이슈 입장", example = "민희진")
    private final String mostVotedStand;

    @Schema(description = "가장 많은 투표를 받은 투표 결과 개수", example = "5342")
    private final int mostVotedCount;

    @Schema(description = "총 투표 수", example = "7000")
    private final int totalVoteCount;

    @Schema(description = "투표 결과 목록")
    private final List<IssueVoteResultResponse> voteResults;

    @Schema(description = "전체 투표 순위")
    private final List<VoteRankingResponse> voteRankings;


    public static IntegratedVoteResultResponse of(final List<IssueStand> stands,
                                                  final Map<IssueStand, Integer> totalVotes,
                                                  final List<Map<IssueStand, Integer>> voteResultResponses,
                                                  final List<VoteRankingResponse> voteRankingResponses) {
        final IssueStand mostVoted = totalVotes.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey).get();

        final int mostVotedCount = totalVotes.getOrDefault(mostVoted, 0);
        final int totalVoteCount = totalVotes.values().stream()
            .mapToInt(Integer::intValue)
            .sum();

        final List<IssueVoteResultResponse> voteResults = voteResultResponses.stream()
            .map(entry -> new IssueVoteResultResponse(entry.get(stands.get(0)), entry.get(stands.get(1))))
            .toList();

        return new IntegratedVoteResultResponse(mostVoted.getStand(), mostVotedCount, totalVoteCount, voteResults,
                                                voteRankingResponses);
    }
}

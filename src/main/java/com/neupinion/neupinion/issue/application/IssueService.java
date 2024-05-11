package com.neupinion.neupinion.issue.application;

import com.neupinion.neupinion.issue.application.dto.IntegratedVoteResultResponse;
import com.neupinion.neupinion.issue.application.dto.VoteRankingResponse;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTrustVote;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTrustVote;
import com.neupinion.neupinion.issue.domain.VoteStatus;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueTrustVoteRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTrustVoteRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class IssueService {

    private final ReprocessedIssueTrustVoteRepository reprocessedIssueTrustVoteRepository;
    private final FollowUpIssueRepository followUpIssueRepository;
    private final FollowUpIssueTrustVoteRepository followUpIssueTrustVoteRepository;

    public IntegratedVoteResultResponse getIntegratedVoteResult(final Long issueId) {
        final List<ReprocessedIssueTrustVote> reprocessedIssueTrustVotes = reprocessedIssueTrustVoteRepository.findByReprocessedIssueId(
            issueId);
        final List<Long> followUpIssueIds = followUpIssueRepository.findByReprocessedIssueId(issueId).stream()
            .map(FollowUpIssue::getId)
            .toList();
        final Map<Long, List<FollowUpIssueTrustVote>> followUpIssueTrustVotes = followUpIssueTrustVoteRepository.findByFollowUpIssueIdIn(
                followUpIssueIds).stream()
            .collect(Collectors.groupingBy(FollowUpIssueTrustVote::getFollowUpIssueId));

        final Map<VoteStatus, Integer> reprocessedIssueTrustVoteCount = reprocessedIssueTrustVotes.stream()
            .collect(Collectors.toMap(ReprocessedIssueTrustVote::getStatus, v -> 1, Integer::sum));

        final Map<Long, Map<VoteStatus, Integer>> followUpIssueTrustVoteCount = followUpIssueTrustVotes.entrySet()
            .stream()
            .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().stream()
                .collect(Collectors.toMap(FollowUpIssueTrustVote::getStatus, v -> 1, Integer::sum))));

        final Map<VoteStatus, Integer> totalVotes = getTotalVotes(reprocessedIssueTrustVoteCount,
                                                                  followUpIssueTrustVoteCount);

        final List<Map<Boolean, Integer>> voteResultResponses = toVoteResults(reprocessedIssueTrustVoteCount, followUpIssueTrustVoteCount);

        final List<VoteRankingResponse> voteRankingResponses = toVoteRankings(totalVotes,
                                                                              reprocessedIssueTrustVoteCount,
                                                                              followUpIssueTrustVoteCount);

        return IntegratedVoteResultResponse.of(totalVotes, voteResultResponses, voteRankingResponses);
    }

    private Map<VoteStatus, Integer> getTotalVotes(final Map<VoteStatus, Integer> reprocessedIssueTrustVoteCount,
                                                   final Map<Long, Map<VoteStatus, Integer>> followUpIssueTrustVoteCount) {
        final Map<VoteStatus, Integer> totalVotes = Arrays.stream(VoteStatus.values())
            .collect(Collectors.toMap(v -> v, v -> 0));

        totalVotes.putAll(reprocessedIssueTrustVoteCount);
        followUpIssueTrustVoteCount.values()
            .forEach(v -> v.forEach((k, count) -> totalVotes.merge(k, count, Integer::sum)));

        return totalVotes;
    }

    private List<Map<Boolean, Integer>> toVoteResults(final Map<VoteStatus, Integer> reprocessedIssueTrustVoteCount,
                                                      final Map<Long, Map<VoteStatus, Integer>> followUpIssueTrustVoteCount) {
        final List<Map<Boolean, Integer>> issueTrustVoteRate = new ArrayList<>();

        mergeByReliability(reprocessedIssueTrustVoteCount, issueTrustVoteRate);

        followUpIssueTrustVoteCount.forEach((k, v) -> {
            mergeByReliability(v, issueTrustVoteRate);
        });

        return issueTrustVoteRate;
    }

    private void mergeByReliability(final Map<VoteStatus, Integer> reprocessedIssueTrustVoteCount,
                                    final List<Map<Boolean, Integer>> issueTrustVoteRate) {
        final int totalReprocessedIssueVotesCount = reprocessedIssueTrustVoteCount.values().stream()
            .mapToInt(Integer::intValue)
            .sum();

        final Map<Boolean, Integer> reprocessedIssuePercentages = Map.of(
            VoteStatus.HIGHLY_TRUSTED.isReliable(),
            (int) ((reprocessedIssueTrustVoteCount.getOrDefault(VoteStatus.HIGHLY_TRUSTED, 0)
                + reprocessedIssueTrustVoteCount.getOrDefault(VoteStatus.SOMEWHAT_TRUSTED, 0))
                / (double) totalReprocessedIssueVotesCount * 100),
            VoteStatus.HIGHLY_DISTRUSTED.isReliable(),
            (int) ((reprocessedIssueTrustVoteCount.getOrDefault(VoteStatus.HIGHLY_DISTRUSTED, 0)
                + reprocessedIssueTrustVoteCount.getOrDefault(VoteStatus.SOMEWHAT_DISTRUSTED, 0))
                / (double) totalReprocessedIssueVotesCount * 100)
        );
        issueTrustVoteRate.add(reprocessedIssuePercentages);
    }

    private List<VoteRankingResponse> toVoteRankings(final Map<VoteStatus, Integer> totalVotes,
                                                     final Map<VoteStatus, Integer> reprocessedIssueTrustVoteCount,
                                                     final Map<Long, Map<VoteStatus, Integer>> followUpIssueTrustVoteCount) {
        final int totalVoteCount = totalVotes.values().stream()
            .mapToInt(Integer::intValue)
            .sum();

        final Map<VoteStatus, Integer> trustVoteCount = new HashMap<>(reprocessedIssueTrustVoteCount);
        followUpIssueTrustVoteCount.values()
            .forEach(v -> v.forEach((k, count) -> trustVoteCount.merge(k, count, Integer::sum)));

        final Map<VoteStatus, Integer> voteRates = trustVoteCount.entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), (int) (entry.getValue() / (double) totalVoteCount * 100)))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        Arrays.stream(VoteStatus.values())
            .filter(v -> !voteRates.containsKey(v) && v != VoteStatus.NOT_VOTED)
            .forEach(v -> voteRates.put(v, 0));

        return voteRates.entrySet().stream()
            .sorted(Entry.<VoteStatus, Integer>comparingByValue().reversed())
            .map(entry -> new VoteRankingResponse(entry.getKey().getValue(), entry.getValue()))
            .collect(Collectors.toList());
    }
}

package com.neupinion.neupinion.issue.application;

import com.neupinion.neupinion.issue.application.dto.IntegratedVoteResultResponse;
import com.neupinion.neupinion.issue.application.dto.TimelineResponse;
import com.neupinion.neupinion.issue.application.dto.VoteRankingResponse;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTrustVote;
import com.neupinion.neupinion.issue.domain.IssueStand;
import com.neupinion.neupinion.issue.domain.IssueType;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTrustVote;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueTrustVoteRepository;
import com.neupinion.neupinion.issue.domain.repository.IssueStandRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTrustVoteRepository;
import com.neupinion.neupinion.issue.exception.ReprocessedIssueException.ReprocessedIssueNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final ReprocessedIssueRepository reprocessedIssueRepository;
    private final IssueStandRepository issueStandRepository;

    public IntegratedVoteResultResponse getIntegratedVoteResult(final Long issueId) {
        final List<ReprocessedIssueTrustVote> reprocessedIssueTrustVotes = reprocessedIssueTrustVoteRepository.findByReprocessedIssueId(
            issueId);
        final List<Long> followUpIssueIds = followUpIssueRepository.findByReprocessedIssueId(issueId).stream()
            .map(FollowUpIssue::getId)
            .toList();
        final Map<Long, List<FollowUpIssueTrustVote>> followUpIssueTrustVotes = followUpIssueTrustVoteRepository.findByFollowUpIssueIdIn(
                followUpIssueIds).stream()
            .collect(Collectors.groupingBy(FollowUpIssueTrustVote::getFollowUpIssueId));

        final List<IssueStand> issueStands = issueStandRepository.findByIssueIdOrderById(issueId);

        final Map<IssueStand, Integer> reprocessedIssueFirstStandCount = Map.of(
            issueStands.get(0),
            reprocessedIssueTrustVotes.stream()
                .filter(v -> v.getRelatableStand().isFirstRelatable())
                .mapToInt(v -> 1)
                .sum(),
            issueStands.get(1),
            reprocessedIssueTrustVotes.stream()
                .filter(v -> v.getRelatableStand().isSecondRelatable())
                .mapToInt(v -> 1)
                .sum()
        );

        final Map<Long, Map<IssueStand, Integer>> followUpIssueTrustVoteCount = new HashMap<>();

        for (final Entry<Long, List<FollowUpIssueTrustVote>> entry : followUpIssueTrustVotes.entrySet()) {
            followUpIssueTrustVoteCount.put(entry.getKey(), Map.of(
                                                issueStands.get(0),
                                                entry.getValue().stream()
                                                    .filter(v -> v.getRelatableStand().isFirstRelatable())
                                                    .mapToInt(v -> 1)
                                                    .sum(),
                                                issueStands.get(1),
                                                entry.getValue().stream()
                                                    .filter(v -> v.getRelatableStand().isSecondRelatable())
                                                    .mapToInt(v -> 1)
                                                    .sum()
                                            )
            );
        }

        final Map<IssueStand, Integer> totalVotes = getTotalVotes(reprocessedIssueFirstStandCount,
                                                                  followUpIssueTrustVoteCount);

        final List<Map<IssueStand, Integer>> voteResultResponses = toVoteResults(issueStands,
                                                                                 reprocessedIssueFirstStandCount,
                                                                                 followUpIssueTrustVoteCount);

        final List<VoteRankingResponse> voteRankingResponses = toVoteRankings(issueStands,
                                                                              totalVotes,
                                                                              reprocessedIssueFirstStandCount,
                                                                              followUpIssueTrustVoteCount);

        return IntegratedVoteResultResponse.of(issueStands, totalVotes, voteResultResponses, voteRankingResponses);
    }

    private Map<IssueStand, Integer> getTotalVotes(final Map<IssueStand, Integer> reprocessedIssueTrustVoteCount,
                                                   final Map<Long, Map<IssueStand, Integer>> followUpIssueTrustVoteCount) {
        final Map<IssueStand, Integer> totalVotes = new HashMap<>(reprocessedIssueTrustVoteCount);

        followUpIssueTrustVoteCount.values()
            .forEach(v -> v.forEach((k, count) -> totalVotes.merge(k, count, Integer::sum)));

        return totalVotes;
    }

    private List<Map<IssueStand, Integer>> toVoteResults(final List<IssueStand> stands,
                                                         final Map<IssueStand, Integer> reprocessedIssueTrustVoteCount,
                                                         final Map<Long, Map<IssueStand, Integer>> followUpIssueTrustVoteCount) {
        final List<Map<IssueStand, Integer>> issueTrustVoteRate = new ArrayList<>();

        mergeByReliability(stands, reprocessedIssueTrustVoteCount, issueTrustVoteRate);

        followUpIssueTrustVoteCount.forEach((k, v) -> mergeByReliability(stands, v, issueTrustVoteRate));

        return issueTrustVoteRate;
    }

    private void mergeByReliability(final List<IssueStand> stands,
                                    final Map<IssueStand, Integer> reprocessedIssueTrustVoteCount,
                                    final List<Map<IssueStand, Integer>> issueTrustVoteRate) {
        final IssueStand firstStand = stands.get(0);
        final IssueStand secondStand = stands.get(1);
        final Map<IssueStand, Integer> reprocessedIssuePercentages = Map.of(
            firstStand,
            reprocessedIssueTrustVoteCount.get(firstStand),
            secondStand,
            reprocessedIssueTrustVoteCount.get(secondStand)
        );
        issueTrustVoteRate.add(reprocessedIssuePercentages);
    }

    private List<VoteRankingResponse> toVoteRankings(final List<IssueStand> stands,
                                                     final Map<IssueStand, Integer> totalVotes,
                                                     final Map<IssueStand, Integer> reprocessedIssueTrustVoteCount,
                                                     final Map<Long, Map<IssueStand, Integer>> followUpIssueTrustVoteCount) {
        final int totalVoteCount = totalVotes.values().stream()
            .mapToInt(Integer::intValue)
            .sum();

        final Map<IssueStand, Integer> trustVoteCount = new HashMap<>(reprocessedIssueTrustVoteCount);
        followUpIssueTrustVoteCount.values()
            .forEach(v -> v.forEach((k, count) -> trustVoteCount.merge(k, count, Integer::sum)));

        final Map<IssueStand, Integer> voteRates = Map.of(
            stands.get(0),
            trustVoteCount.get(stands.get(0)) * 100 / totalVoteCount,
            stands.get(1),
            trustVoteCount.get(stands.get(1)) * 100 / totalVoteCount
        );

        return voteRates.entrySet().stream()
            .sorted(Entry.<IssueStand, Integer>comparingByValue().reversed())
            .map(entry -> new VoteRankingResponse(entry.getKey().getStand(), entry.getValue()))
            .collect(Collectors.toList());
    }

    public List<TimelineResponse> getIssueTimeLine(final Long issueId) {
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.findById(issueId)
            .orElseThrow(ReprocessedIssueNotFoundException::new);
        final List<FollowUpIssue> followUpIssues = followUpIssueRepository.findByReprocessedIssueId(issueId).stream()
            .sorted(Comparator.comparing(FollowUpIssue::getCreatedAt))
            .toList();

        final List<TimelineResponse> timelineResponses = new ArrayList<>();

        timelineResponses.add(
            new TimelineResponse(IssueType.REPROCESSED.name(), reprocessedIssue.getId(), reprocessedIssue.getTitle(),
                                 reprocessedIssue.getCreatedAt()));
        followUpIssues.forEach(followUpIssue -> timelineResponses.add(
            new TimelineResponse(IssueType.FOLLOW_UP.name(), followUpIssue.getId(), followUpIssue.getTitle().getValue(),
                                 followUpIssue.getCreatedAt())));

        return timelineResponses;
    }
}

package com.neupinion.neupinion.issue.application;

import com.neupinion.neupinion.bookmark.domain.repository.ReprocessedIssueBookmarkRepository;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssuesByReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.RecentReprocessedIssueByCategoryResponse;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.ReprocessedIssueVoteResultResponse;
import com.neupinion.neupinion.issue.application.dto.ShortReprocessedIssueResponse;
import com.neupinion.neupinion.issue.application.dto.TrustVoteRequest;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTag;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTrustVote;
import com.neupinion.neupinion.issue.domain.VoteStatus;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTagRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTrustVoteRepository;
import com.neupinion.neupinion.issue.domain.repository.dto.ReprocessedIssueWithCommentCount;
import com.neupinion.neupinion.issue.exception.ReprocessedIssueException.ReprocessedIssueNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReprocessedIssueService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int REPROCESSED_ISSUES_SIZE = 4;
    private static final int FOLLOW_UP_ISSUE_PAGE_SIZE = 3;
    private static final int INTEGRATED_VOTE_MINIMUM = 3;

    private final ReprocessedIssueRepository reprocessedIssueRepository;
    private final ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;
    private final ReprocessedIssueTagRepository reprocessedIssueTagRepository;
    private final ReprocessedIssueBookmarkRepository reprocessedIssueBookmarkRepository;
    private final ReprocessedIssueTrustVoteRepository reprocessedIssueTrustVoteRepository;
    private final FollowUpIssueRepository followUpIssueRepository;

    @Transactional
    public Long createReprocessedIssue(final ReprocessedIssueCreateRequest request) {
        final ReprocessedIssue reprocessedIssue = ReprocessedIssue.forSave(request.getTitle(),
                                                                           request.getImageUrl(),
                                                                           request.getCaption(),
                                                                           request.getOriginUrl(),
                                                                           Category.from(request.getCategory()), request.getTopic());

        return reprocessedIssueRepository.save(reprocessedIssue).getId();
    }

    public List<ShortReprocessedIssueResponse> findReprocessedIssues(final String dateFormat) {
        final LocalDate targetDate = LocalDate.parse(dateFormat, FORMATTER);
        final PageRequest pageRequest = PageRequest.of(0, REPROCESSED_ISSUES_SIZE, Sort.by("createdAt").descending());
        final List<ReprocessedIssueWithCommentCount> issuesWithCommentCount = reprocessedIssueRepository.findByCreatedAt(
            targetDate, pageRequest);

        return ShortReprocessedIssueResponse.of(issuesWithCommentCount);
    }

    public ReprocessedIssueResponse findReprocessedIssue(final Long memberId, final Long id) {
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.findById(id)
            .orElseThrow(ReprocessedIssueNotFoundException::new);
        final List<ReprocessedIssueParagraph> paragraphs = reprocessedIssueParagraphRepository.findByReprocessedIssueIdOrderById(
            id);
        final List<String> tags = reprocessedIssueTagRepository.findByReprocessedIssueId(id).stream()
            .map(ReprocessedIssueTag::getTag)
            .toList();
        final boolean isBookmarked = reprocessedIssueBookmarkRepository.existsByReprocessedIssueIdAndMemberIdAndIsBookmarkedIsTrue(
            id, memberId);
        final Optional<ReprocessedIssueTrustVote> trustVote = reprocessedIssueTrustVoteRepository.findByReprocessedIssueIdAndMemberId(
            id, memberId);

        return trustVote.map(reprocessedIssueTrustVote -> ReprocessedIssueResponse.of(reprocessedIssue, isBookmarked,
                                                                                      reprocessedIssueTrustVote.getStatus(),
                                                                                      paragraphs, tags)).orElseGet(
            () -> ReprocessedIssueResponse.of(reprocessedIssue, isBookmarked, VoteStatus.NOT_VOTED, paragraphs, tags)
        );
    }

    @Transactional
    public void vote(final Long memberId, final Long id, final TrustVoteRequest request) {
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.findById(id)
            .orElseThrow(ReprocessedIssueNotFoundException::new);
        final ReprocessedIssueTrustVote trustVote = reprocessedIssueTrustVoteRepository.findByReprocessedIssueIdAndMemberId(
                id, memberId)
            .orElseGet(() -> {
                final ReprocessedIssueTrustVote newTrustVote = ReprocessedIssueTrustVote.forSave(
                    reprocessedIssue.getId(),
                    memberId,
                    request.getStatus());
                return reprocessedIssueTrustVoteRepository.save(newTrustVote);
            });

        trustVote.updateStatus(request.getStatus());
    }

    public List<RecentReprocessedIssueByCategoryResponse> findReprocessedIssuesByCategory(final Long id,
                                                                                          final String category) {
        return reprocessedIssueRepository.findCurrentReprocessedIssuesByCategory(
                Category.from(category), id).stream()
            .map(RecentReprocessedIssueByCategoryResponse::of)
            .toList();
    }

    public ReprocessedIssueVoteResultResponse getVoteResult(final Long id) {
        final List<ReprocessedIssueTrustVote> votes = reprocessedIssueTrustVoteRepository.findByReprocessedIssueId(id);
        final Map<VoteStatus, Integer> votesCount = votes.stream()
            .collect(Collectors.toMap(ReprocessedIssueTrustVote::getStatus, v -> 1, Integer::sum));
        final int totalVotesCount = votesCount.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
        final Map<VoteStatus, Integer> percentages = votesCount.entrySet().stream()
            .sorted(Entry.comparingByValue())
            .map(entry -> Map.entry(entry.getKey(), (int) (entry.getValue() / (double) totalVotesCount * 100)))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        Arrays.stream(VoteStatus.values())
            .filter(voteStatus -> !percentages.containsKey(voteStatus) && voteStatus != VoteStatus.NOT_VOTED)
            .forEach(voteStatus -> percentages.put(voteStatus, 0));

        return ReprocessedIssueVoteResultResponse.of(votesCount, percentages);
    }

    public FollowUpIssuesByReprocessedIssueResponse getFollowUpIssues(final Long id) {
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.findById(id)
            .orElseThrow(ReprocessedIssueNotFoundException::new);
        final List<FollowUpIssue> followUpIssues = followUpIssueRepository.findByReprocessedIssueIdOrderByCreatedAtDesc(
            id, PageRequest.of(0, FOLLOW_UP_ISSUE_PAGE_SIZE));

        return FollowUpIssuesByReprocessedIssueResponse.of(reprocessedIssue, followUpIssues,
                                                           followUpIssues.size() >= INTEGRATED_VOTE_MINIMUM);
    }
}

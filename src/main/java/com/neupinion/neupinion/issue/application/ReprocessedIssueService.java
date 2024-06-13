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
import com.neupinion.neupinion.issue.domain.IssueStand;
import com.neupinion.neupinion.issue.domain.IssueStandReference;
import com.neupinion.neupinion.issue.domain.RelatableStand;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTag;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueTrustVote;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.IssueStandReferenceRepository;
import com.neupinion.neupinion.issue.domain.repository.IssueStandRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTagRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueTrustVoteRepository;
import com.neupinion.neupinion.issue.domain.repository.dto.ReprocessedIssueWithCommentCount;
import com.neupinion.neupinion.issue.exception.ReprocessedIssueException.ReprocessedIssueNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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
    private final IssueStandReferenceRepository issueStandReferenceRepository;
    private final ReprocessedIssueTagRepository reprocessedIssueTagRepository;
    private final ReprocessedIssueBookmarkRepository reprocessedIssueBookmarkRepository;
    private final ReprocessedIssueTrustVoteRepository reprocessedIssueTrustVoteRepository;
    private final IssueStandRepository issueStandRepository;
    private final FollowUpIssueRepository followUpIssueRepository;

    @Transactional
    public Long createReprocessedIssue(final ReprocessedIssueCreateRequest request) {
        final ReprocessedIssue reprocessedIssue = ReprocessedIssue.forSave(request.getTitle(),
                                                                           request.getImageUrl(),
                                                                           request.getCaption(),
                                                                           Category.from(request.getCategory()));

        final Long issueId = reprocessedIssueRepository.save(reprocessedIssue).getId();
        final List<IssueStand> savedIssueStands = issueStandRepository.saveAll(
            request.getStands().stream()
                .map(stand -> IssueStand.forSave(stand, issueId))
                .toList()
        );

        issueStandReferenceRepository.saveAll(
            request.getFirstIssueStandReferences().stream()
                .map(stand -> IssueStandReference.forSave(savedIssueStands.get(0).getId(), stand))
                .toList()
        );

        issueStandReferenceRepository.saveAll(
            request.getSecondIssueStandReferences().stream()
                .map(stand -> IssueStandReference.forSave(savedIssueStands.get(1).getId(), stand))
                .toList()
        );

        reprocessedIssueParagraphRepository.saveAll(
            request.getParagraphs().stream()
                .map(paragraph -> ReprocessedIssueParagraph.forSave(paragraph.getContent(), paragraph.getIsSelectable(),
                                                                    issueId))
                .toList()
        );

        return issueId;
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
            .orElseThrow(
                ReprocessedIssueNotFoundException::new);
        final List<ReprocessedIssueParagraph> paragraphs = reprocessedIssueParagraphRepository.findByReprocessedIssueIdOrderById(
            id);
        final List<String> tags = reprocessedIssueTagRepository.findByReprocessedIssueId(id).stream()
            .map(ReprocessedIssueTag::getTag)
            .toList();
        final boolean isBookmarked = reprocessedIssueBookmarkRepository.existsByReprocessedIssueIdAndMemberIdAndIsBookmarkedIsTrue(
            id, memberId);
        final Optional<ReprocessedIssueTrustVote> trustVote = reprocessedIssueTrustVoteRepository.findByReprocessedIssueIdAndMemberId(
            id, memberId);
        final List<IssueStand> stands = issueStandRepository.findByIssueIdOrderById(id);
        final RelatableStand relatableStand = trustVote.map(ReprocessedIssueTrustVote::getRelatableStand)
            .orElse(RelatableStand.NOT_VOTED);
        final List<IssueStandReference> references = issueStandReferenceRepository.findByIssueStandIdIn(
            stands.stream()
                .map(IssueStand::getId)
                .toList());

        return ReprocessedIssueResponse.of(reprocessedIssue, isBookmarked, stands, references,
                                           relatableStand != RelatableStand.NOT_VOTED, relatableStand, paragraphs,
                                           tags);
    }

    @Transactional
    public void vote(final Long memberId, final Long id, final TrustVoteRequest request) {
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.findById(id)
            .orElseThrow(
                ReprocessedIssueNotFoundException::new);
        final ReprocessedIssueTrustVote trustVote = reprocessedIssueTrustVoteRepository.findByReprocessedIssueIdAndMemberId(
                id, memberId)
            .orElseGet(() -> {
                final RelatableStand relatableStand = new RelatableStand(
                    request.getFirstStandId(),
                    request.getFirstRelatable(),
                    request.getSecondStandId(),
                    request.getSecondRelatable());
                final ReprocessedIssueTrustVote newTrustVote = ReprocessedIssueTrustVote.forSave(
                    reprocessedIssue.getId(),
                    memberId,
                    relatableStand);
                return reprocessedIssueTrustVoteRepository.save(
                    newTrustVote);
            });

        trustVote.updateSelectedStand(request.getFirstStandId(), request.getFirstRelatable(),
                                      request.getSecondStandId(), request.getSecondRelatable());
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
        final List<IssueStand> stands = issueStandRepository.findByIssueIdOrderById(id);
        final Map<Boolean, Integer> firstVotesCount = votes.stream()
            .collect(Collectors.toMap(
                vote -> vote.getRelatableStand().isFirstRelatable(),
                v -> 1,
                Integer::sum));
        final Map<Boolean, Integer> secondVotesCount = votes.stream()
            .collect(Collectors.toMap(
                vote -> vote.getRelatableStand().isSecondRelatable(),
                v -> 1,
                Integer::sum));
        final int totalVotesCount = firstVotesCount.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
        final Map<IssueStand, Integer> percentages = Map.of(
            stands.get(0),
            (int) ((double) firstVotesCount.getOrDefault(true, 0) / totalVotesCount * 100),
            stands.get(1),
            (int) ((double) secondVotesCount.getOrDefault(true, 0) / totalVotesCount * 100)
        );

        final int maxVotedCount = Math.max(firstVotesCount.getOrDefault(true, 0),
                                           secondVotesCount.getOrDefault(true, 0));
        return ReprocessedIssueVoteResultResponse.of(percentages, maxVotedCount, totalVotesCount);
    }

    public FollowUpIssuesByReprocessedIssueResponse getFollowUpIssues(final Long id) {
        final ReprocessedIssue reprocessedIssue = reprocessedIssueRepository.findById(id)
            .orElseThrow(
                ReprocessedIssueNotFoundException::new);
        final List<FollowUpIssue> followUpIssues = followUpIssueRepository.findByReprocessedIssueIdOrderByCreatedAtDesc(
            id, PageRequest.of(0, FOLLOW_UP_ISSUE_PAGE_SIZE));

        return FollowUpIssuesByReprocessedIssueResponse.of(reprocessedIssue, followUpIssues,
                                                           followUpIssues.size() >= INTEGRATED_VOTE_MINIMUM);
    }
}

package com.neupinion.neupinion.opinion.application;

import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.member.domain.Member;
import com.neupinion.neupinion.member.domain.repository.MemberRepository;
import com.neupinion.neupinion.opinion.application.dto.IssueOpinionResponse;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinionLike;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinionLike;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionRepository;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class IntegratedOpinionService {

    private static final int TOP_OPINION_COUNT = 5;

    private final MemberRepository memberRepository;
    private final FollowUpIssueRepository followUpIssueRepository;
    private final ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;
    private final ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;
    private final FollowUpIssueOpinionRepository followUpIssueOpinionRepository;
    private final FollowUpIssueParagraphRepository followUpIssueParagraphRepository;

    public List<IssueOpinionResponse> getTopOpinions(final Long issueId, final Long memberId) {
        final Member member = memberRepository.getById(memberId);

        final List<FollowUpIssue> followUpIssues = followUpIssueRepository.findByReprocessedIssueId(issueId);
        final List<Long> followUpIssueIds = followUpIssues.stream()
            .map(FollowUpIssue::getId)
            .toList();

        final PageRequest pageable = PageRequest.of(0, TOP_OPINION_COUNT);
        final List<ReprocessedIssueOpinion> reprocessedIssueOpinions = reprocessedIssueOpinionRepository.findTop5ByActiveLikes(
            pageable, issueId);
        final List<FollowUpIssueOpinion> followUpIssueOpinions = followUpIssueOpinionRepository.findTop5Opinions(
            followUpIssueIds, pageable);

        final Comparator<Object> byLikes = Comparator.comparing(opinion -> {
            if (opinion instanceof ReprocessedIssueOpinion) {
                return ((ReprocessedIssueOpinion) opinion).getLikes().size();
            } else if (opinion instanceof FollowUpIssueOpinion) {
                return ((FollowUpIssueOpinion) opinion).getLikes().size();
            }
            return 0;
        }).reversed();

        final List<IssueOpinionResponse> responses = Stream.concat(reprocessedIssueOpinions.stream(),
                                                                   followUpIssueOpinions.stream())
            .sorted(byLikes)
            .map(opinion -> {
                if (opinion instanceof ReprocessedIssueOpinion) {
                    return toDto((ReprocessedIssueOpinion) opinion, member);
                }
                return toDto((FollowUpIssueOpinion) opinion, member);
            })
            .toList();

        return responses.subList(0, Math.min(TOP_OPINION_COUNT, responses.size()));
    }

    private IssueOpinionResponse toDto(final ReprocessedIssueOpinion opinion, final Member member) {
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.getById(
            opinion.getParagraphId());
        final List<ReprocessedIssueOpinionLike> likes = opinion.getLikes().stream()
            .filter(like -> !like.getIsDeleted())
            .toList();
        final boolean isLiked = likes.stream()
            .anyMatch(like -> like.getMemberId().equals(member.getId()));

        return IssueOpinionResponse.of(opinion, member, paragraph, isLiked);
    }

    private IssueOpinionResponse toDto(final FollowUpIssueOpinion opinion, final Member member) {
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.getById(
            opinion.getParagraphId());
        final List<FollowUpIssueOpinionLike> likes = opinion.getLikes().stream()
            .filter(like -> !like.getIsDeleted())
            .toList();
        final boolean isLiked = likes.stream()
            .anyMatch(like -> like.getMemberId().equals(member.getId()));

        return IssueOpinionResponse.of(opinion, member, paragraph, isLiked);
    }
}

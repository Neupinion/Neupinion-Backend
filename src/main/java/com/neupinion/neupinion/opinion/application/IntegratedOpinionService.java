package com.neupinion.neupinion.opinion.application;

import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueParagraph;
import com.neupinion.neupinion.issue.domain.IssueType;
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
import com.neupinion.neupinion.opinion.domain.repository.dto.IssueOpinionMapping;
import com.neupinion.neupinion.query_mode.order.AllOpinionOrderStrategy;
import com.neupinion.neupinion.query_mode.order.OrderMode;
import com.neupinion.neupinion.query_mode.view.opinion.OpinionViewMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    private static final int PAGE_SIZE = 10;

    private final MemberRepository memberRepository;
    private final ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;
    private final ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;
    private final FollowUpIssueRepository followUpIssueRepository;
    private final FollowUpIssueOpinionRepository followUpIssueOpinionRepository;
    private final FollowUpIssueParagraphRepository followUpIssueParagraphRepository;

    private final Map<OpinionViewMode, List<Boolean>> opinionViewStrategies;
    private final Map<OrderMode, AllOpinionOrderStrategy> allOrderStrategies;

    public List<IssueOpinionResponse> getTopOpinionsByLikes(final Long issueId, final Long memberId) {
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
                    return toDto((ReprocessedIssueOpinion) opinion, memberId);
                }
                return toDto((FollowUpIssueOpinion) opinion, memberId);
            })
            .toList();

        return responses.subList(0, Math.min(TOP_OPINION_COUNT, responses.size()));
    }

    private IssueOpinionResponse toDto(final ReprocessedIssueOpinion opinion, final Long currentMemberId) {
        final Member writer = memberRepository.getById(opinion.getMemberId());
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.getById(
            opinion.getParagraphId());
        final List<ReprocessedIssueOpinionLike> likes = opinion.getLikes().stream()
            .filter(like -> !like.getIsDeleted())
            .toList();
        final boolean isLiked = likes.stream()
            .anyMatch(like -> like.getMemberId().equals(currentMemberId));

        return IssueOpinionResponse.of(opinion, writer, paragraph, isLiked);
    }

    private IssueOpinionResponse toDto(final FollowUpIssueOpinion opinion, final Long currentMemberId) {
        final Member writer = memberRepository.getById(opinion.getMemberId());
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.getById(
            opinion.getParagraphId());
        final List<FollowUpIssueOpinionLike> likes = opinion.getLikes().stream()
            .filter(like -> !like.getIsDeleted())
            .toList();
        final boolean isLiked = likes.stream()
            .anyMatch(like -> like.getMemberId().equals(currentMemberId));

        return IssueOpinionResponse.of(opinion, writer, paragraph, isLiked);
    }

    public List<IssueOpinionResponse> getIntegratedOpinions(final Long issueId, final Long memberId,
                                                            final OrderMode orderMode, final OpinionViewMode viewMode) {
        final List<Boolean> reliabilities = opinionViewStrategies.get(viewMode);
        final AllOpinionOrderStrategy orderStrategy = allOrderStrategies.get(orderMode);

        final List<IssueOpinionMapping> issueOpinionMappings =
            orderStrategy.getOpinionsByReliabilitiesOrderBy(issueId, reliabilities, PageRequest.of(0, PAGE_SIZE));

        return issueOpinionMappings.stream()
            .map(mapping -> toDto(mapping, memberId))
            .toList();
    }

    private IssueOpinionResponse toDto(final IssueOpinionMapping mapping, final Long currentMemberId) {
        final Member writer = memberRepository.getById(mapping.writerId());

        if (IssueType.isReprocessed(mapping.issueType())) {
            final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.getById(
                mapping.paragraphId());
            final boolean isLiked = reprocessedIssueOpinionRepository.existsByMemberIdAndParagraphId(currentMemberId,
                                                                                                     mapping.paragraphId());
            return IssueOpinionResponse.of(mapping, writer, paragraph, isLiked);
        }
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.getById(mapping.paragraphId());
        final boolean isLiked = followUpIssueOpinionRepository.existsByMemberIdAndParagraphId(currentMemberId,
                                                                                              mapping.paragraphId());
        return IssueOpinionResponse.of(mapping, writer, paragraph, isLiked);
    }
}

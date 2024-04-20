package com.neupinion.neupinion.opinion.application;

import com.neupinion.neupinion.issue.domain.FollowUpIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.issue.exception.ParagraphException;
import com.neupinion.neupinion.member.domain.Member;
import com.neupinion.neupinion.member.domain.repository.MemberRepository;
import com.neupinion.neupinion.opinion.application.dto.FollowUpIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.MyOpinionResponse;
import com.neupinion.neupinion.opinion.application.dto.OpinionUpdateRequest;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionResponse;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinionLike;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionRepository;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import com.neupinion.neupinion.opinion.exception.OpinionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OpinionService {

    private final FollowUpIssueOpinionRepository followUpIssueOpinionRepository;
    private final FollowUpIssueParagraphRepository followUpIssueParagraphRepository;
    private final ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;
    private final ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createFollowUpIssueOpinion(final Long memberId, final FollowUpIssueOpinionCreateRequest request) {
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.getById(request.getParagraphId());
        validateSameParagraphAndMember(memberId, request);
        validateParagraphForSameIssue(request, paragraph);

        final FollowUpIssueOpinion savedFollowUpIssueOpinion = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(request.getParagraphId(), request.getFollowUpIssueId(),
                                         request.getIsReliable(), memberId, request.getContent()));
        return savedFollowUpIssueOpinion.getId();
    }

    private void validateSameParagraphAndMember(final Long memberId, final FollowUpIssueOpinionCreateRequest request) {
        final boolean isExisted = followUpIssueOpinionRepository.existsByMemberIdAndParagraphId(memberId,
                                                                                                request.getParagraphId());

        if (isExisted) {
            throw new OpinionException.AlreadyExistedOpinionException(
                Map.of("memberId", memberId.toString(),
                       "paragraphId", request.getParagraphId().toString(),
                       "type", "followUpIssue"
                )
            );
        }
    }

    private void validateParagraphForSameIssue(final FollowUpIssueOpinionCreateRequest request,
                                               final FollowUpIssueParagraph followUpIssueParagraph) {
        if (!followUpIssueParagraph.getFollowUpIssueId().equals(request.getFollowUpIssueId())) {
            throw new ParagraphException.ParagraphForOtherIssueException(
                Map.of("paragraphId", request.getParagraphId().toString(),
                       "followUpIssueId", request.getFollowUpIssueId().toString()
                ));
        }
    }

    @Transactional
    public Long createReprocessedIssueOpinion(final Long memberId,
                                              final ReprocessedIssueOpinionCreateRequest request) {
        validateSameParagraphAndMember(memberId, request);
        final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.getById(
            request.getParagraphId());
        validateParagraphForSameIssue(request, paragraph);

        final ReprocessedIssueOpinion savedReprocessedIssueOpinion = reprocessedIssueOpinionRepository.save(
            ReprocessedIssueOpinion.forSave(request.getParagraphId(), request.getReprocessedIssueId(),
                                            request.getIsReliable(), memberId, request.getContent()));

        return savedReprocessedIssueOpinion.getId();
    }

    private void validateSameParagraphAndMember(final Long memberId,
                                                final ReprocessedIssueOpinionCreateRequest request) {
        final boolean isExisted = reprocessedIssueOpinionRepository.existsByMemberIdAndParagraphId(memberId,
                                                                                                   request.getParagraphId());

        if (isExisted) {
            throw new OpinionException.AlreadyExistedOpinionException(
                Map.of("memberId", memberId.toString(),
                       "paragraphId", request.getParagraphId().toString(),
                       "type", "reprocessedIssue"
                ));
        }
    }

    private void validateParagraphForSameIssue(final ReprocessedIssueOpinionCreateRequest request,
                                               final ReprocessedIssueParagraph reprocessedIssueParagraph) {
        if (!reprocessedIssueParagraph.getReprocessedIssueId().equals(request.getReprocessedIssueId())) {
            throw new ParagraphException.ParagraphForOtherIssueException(
                Map.of("paragraphId", request.getParagraphId().toString(),
                       "reprocessedIssueId", request.getReprocessedIssueId().toString(),
                       "type", "reprocessedIssue"
                ));
        }
    }

    @Transactional
    public void updateReprocessedIssueOpinion(final Long memberId, final Long opinionId,
                                              final OpinionUpdateRequest request) {
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.getById(opinionId);

        validateMatchedMember(memberId, opinion);
        validateParagraphForSameIssue(request, opinion);

        opinion.update(request.getParagraphId(), request.getContent(), request.getIsReliable());
    }

    private void validateMatchedMember(final Long memberId, final ReprocessedIssueOpinion opinion) {
        if (!opinion.getMemberId().equals(memberId)) {
            throw new OpinionException.NotMatchedMemberException(
                Map.of("memberId", memberId.toString(),
                       "opinionId", opinion.getId().toString()
                ));
        }
    }

    private void validateParagraphForSameIssue(final OpinionUpdateRequest request,
                                               final ReprocessedIssueOpinion opinion) {
        final ReprocessedIssueParagraph reprocessedIssueParagraph = reprocessedIssueParagraphRepository.getById(
            request.getParagraphId());

        if (!reprocessedIssueParagraph.getReprocessedIssueId().equals(opinion.getReprocessedIssueId())) {
            throw new ParagraphException.ParagraphForOtherIssueException(
                Map.of("paragraphId", request.getParagraphId().toString(),
                       "reprocessedIssueId", opinion.getReprocessedIssueId().toString(),
                       "type", "reprocessedIssue"
                ));
        }
    }

    @Transactional
    public void updateFollowUpIssueOpinion(final Long memberId, final Long opinionId,
                                           final OpinionUpdateRequest request) {
        final FollowUpIssueOpinion opinion = followUpIssueOpinionRepository.getById(opinionId);

        validateMatchedMember(memberId, opinion);
        validateParagraphForSameIssue(request, opinion);

        opinion.update(request.getParagraphId(), request.getContent(), request.getIsReliable());
    }

    private void validateMatchedMember(final Long memberId, final FollowUpIssueOpinion opinion) {
        if (!opinion.getMemberId().equals(memberId)) {
            throw new OpinionException.NotMatchedMemberException(
                Map.of("memberId", memberId.toString(),
                       "opinionId", opinion.getId().toString()
                ));
        }
    }

    private void validateParagraphForSameIssue(final OpinionUpdateRequest request, final FollowUpIssueOpinion opinion) {
        final FollowUpIssueParagraph followUpIssueParagraph = followUpIssueParagraphRepository.getById(
            request.getParagraphId());

        if (!followUpIssueParagraph.getFollowUpIssueId().equals(opinion.getFollowUpIssueId())) {
            throw new ParagraphException.ParagraphForOtherIssueException(
                Map.of("paragraphId", request.getParagraphId().toString(),
                       "followUpIssueId", opinion.getFollowUpIssueId().toString(),
                       "type", "followUpIssue"
                ));
        }
    }

    public List<MyOpinionResponse> getMyFollowUpOpinions(final Long memberId, final Long issueId) {
        final List<FollowUpIssueOpinion> opinions = followUpIssueOpinionRepository.findByMemberIdAndFollowUpIssueId(
            memberId, issueId);

        return opinions.stream()
            .map(opinion -> {
                final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.getById(
                    opinion.getParagraphId());
                return MyOpinionResponse.from(opinion, paragraph.getContent());
            })
            .toList();
    }

    public List<MyOpinionResponse> getMyReprocessedOpinions(final Long memberId, final Long issueId) {
        final List<ReprocessedIssueOpinion> opinions = reprocessedIssueOpinionRepository.findByMemberIdAndReprocessedIssueId(
            memberId, issueId);

        return opinions.stream()
            .map(opinion -> {
                final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.getById(
                    opinion.getParagraphId());
                return MyOpinionResponse.from(opinion, paragraph.getContent());
            })
            .toList();
    }

    @Transactional
    public void deleteFollowUpIssueOpinion(final Long memberId, final Long opinionId) {
        final FollowUpIssueOpinion opinion = followUpIssueOpinionRepository.getById(opinionId);
        validateMatchedMember(memberId, opinion);

        followUpIssueOpinionRepository.delete(opinion);
    }

    @Transactional
    public void deleteReprocessedIssueOpinion(final Long memberId, final Long opinionId) {
        final ReprocessedIssueOpinion opinion = reprocessedIssueOpinionRepository.getById(opinionId);
        validateMatchedMember(memberId, opinion);

        reprocessedIssueOpinionRepository.delete(opinion);
    }

    public List<ReprocessedIssueOpinionResponse> getReprocessedIssueOpinions(final Long issueId, final long memberId) {
        final List<ReprocessedIssueOpinion> opinions = reprocessedIssueOpinionRepository.findByReprocessedIssueIdWithLikes(issueId);

        List<ReprocessedIssueOpinionResponse> responses = new ArrayList<>();
        for (ReprocessedIssueOpinion opinion : opinions) {
            final List<ReprocessedIssueOpinionLike> likes = opinion.getLikes().stream()
                .filter(like -> !like.getIsDeleted())
                .toList();
            final ReprocessedIssueParagraph paragraph = reprocessedIssueParagraphRepository.getById(
                opinion.getParagraphId());
            final Member member = memberRepository.getMemberById(opinion.getMemberId());
            final int likeCount = likes.size();
            final boolean isLiked = likes.stream()
                .anyMatch(like -> like.getMemberId().equals(memberId));

            responses.add(ReprocessedIssueOpinionResponse.of(opinion, likeCount, member, paragraph, isLiked));
        }

        return responses;
    }
}

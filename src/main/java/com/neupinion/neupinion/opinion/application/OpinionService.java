package com.neupinion.neupinion.opinion.application;

import com.neupinion.neupinion.issue.domain.FollowUpIssueParagraph;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueParagraphRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import com.neupinion.neupinion.issue.exception.ParagraphException;
import com.neupinion.neupinion.opinion.application.dto.FollowUpIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.application.dto.ReprocessedIssueOpinionCreateRequest;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionRepository;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionRepository;
import com.neupinion.neupinion.opinion.exception.OpinionException;
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

    @Transactional
    public Long createFollowUpIssueOpinion(final Long memberId, final FollowUpIssueOpinionCreateRequest request) {
        final FollowUpIssueParagraph paragraph = followUpIssueParagraphRepository.getById(request.getParagraphId());
        validateSameParagraphAndMember(memberId, request);
        validateParagraphForSameIssue(request, paragraph);

        final FollowUpIssueOpinion savedFollowUpIssueOpinion = followUpIssueOpinionRepository.save(
            FollowUpIssueOpinion.forSave(request.getParagraphId(), request.getFollowUpIssueId(), memberId,
                                         request.getContent()));
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
            ReprocessedIssueOpinion.forSave(request.getParagraphId(), request.getReprocessedIssueId(), memberId,
                                            request.getContent()));

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
}

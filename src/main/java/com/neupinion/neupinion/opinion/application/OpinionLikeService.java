package com.neupinion.neupinion.opinion.application;

import com.neupinion.neupinion.opinion.application.dto.OpinionLikeRequest;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinionLike;
import com.neupinion.neupinion.opinion.domain.repository.ReprocessedIssueOpinionLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OpinionLikeService {

    private final ReprocessedIssueOpinionLikeRepository reprocessedIssueOpinionLikeRepository;

    @Transactional
    public void updateLike(final Long opinionId, final OpinionLikeRequest request,
                           final Long memberId) {
        final ReprocessedIssueOpinionLike opinionLike = reprocessedIssueOpinionLikeRepository.findByMemberIdAndReprocessedIssueOpinionId(
                memberId, opinionId)
            .orElse(reprocessedIssueOpinionLikeRepository.saveAndFlush(
                ReprocessedIssueOpinionLike.forSave(memberId, opinionId)
            ));

        if (opinionLike.getIsDeleted() == request.getIsLiked()) {
            opinionLike.updateDeletionStatus();
        }
    }
}

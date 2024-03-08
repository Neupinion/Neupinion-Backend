package com.neupinion.neupinion.opinion.domain.repository;

import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.exception.OpinionException.NotFoundOpinionException;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowUpIssueOpinionRepository extends JpaRepository<FollowUpIssueOpinion, Long> {

    Set<FollowUpIssueOpinion> findByMemberId(final Long memberId);

    List<FollowUpIssueOpinion> findByMemberIdAndFollowUpIssueId(final Long memberId, final Long followUpIssueId);

    boolean existsByMemberIdAndParagraphId(final Long memberId, final Long paragraphId);

    default FollowUpIssueOpinion getById(final Long id) {
        return findById(id)
            .orElseThrow(NotFoundOpinionException::new);
    }
}

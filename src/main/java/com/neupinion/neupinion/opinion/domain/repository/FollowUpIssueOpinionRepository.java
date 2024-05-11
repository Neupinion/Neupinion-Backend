package com.neupinion.neupinion.opinion.domain.repository;

import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.exception.OpinionException.NotFoundOpinionException;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FollowUpIssueOpinionRepository extends JpaRepository<FollowUpIssueOpinion, Long> {

    Set<FollowUpIssueOpinion> findByMemberId(final Long memberId);

    List<FollowUpIssueOpinion> findByMemberIdAndFollowUpIssueId(final Long memberId, final Long followUpIssueId);

    boolean existsByMemberIdAndParagraphId(final Long memberId, final Long paragraphId);

    default FollowUpIssueOpinion getById(final Long id) {
        return findById(id)
            .orElseThrow(NotFoundOpinionException::new);
    }

    @Query("SELECT f "
        + "FROM FollowUpIssueOpinion f "
        + "LEFT JOIN f.likes l "
        + "WHERE l.isDeleted = false AND f.followUpIssueId IN :followUpIssueIds "
        + "GROUP BY f.id "
        + "ORDER BY COUNT(l) DESC, f.id DESC")
    List<FollowUpIssueOpinion> findTop5Opinions(List<Long> followUpIssueIds, Pageable pageable);
}

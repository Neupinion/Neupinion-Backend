package com.neupinion.neupinion.opinion.domain.repository;

import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.opinion.exception.OpinionException.NotFoundOpinionException;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReprocessedIssueOpinionRepository extends JpaRepository<ReprocessedIssueOpinion, Long> {

    boolean existsByMemberIdAndParagraphId(final Long memberId, final Long paragraphId);

    default ReprocessedIssueOpinion getById(final Long id) {
        return findById(id)
            .orElseThrow(NotFoundOpinionException::new);
    }

    List<ReprocessedIssueOpinion> findByMemberIdAndReprocessedIssueId(final Long memberId,
                                                                      final Long reprocessedIssueId);

    @Query(value = "SELECT r "
        + "FROM ReprocessedIssueOpinion r "
        + "LEFT JOIN FETCH ReprocessedIssueOpinionLike l ON r.id = l.reprocessedIssueOpinionId "
        + "WHERE r.reprocessedIssueId = :issueId "
        + "ORDER BY r.createdAt DESC")
    List<ReprocessedIssueOpinion> findByReprocessedIssueIdWithLikes(final Long issueId);

    @Query(value = "SELECT r "
        + "FROM ReprocessedIssueOpinion r "
        + "LEFT JOIN FETCH ReprocessedIssueOpinionLike l ON r.id = l.reprocessedIssueOpinionId "
        + "WHERE r.reprocessedIssueId = :issueId AND r.isReliable = :isReliable "
        + "ORDER BY r.createdAt DESC")
    List<ReprocessedIssueOpinion> findByIssueIdAndIsReliableWithLikes(final Long issueId, final boolean isReliable);

    @Query("SELECT r "
        + "FROM ReprocessedIssueOpinion r "
        + "LEFT JOIN r.likes l "
        + "WHERE l.isDeleted = false AND r.reprocessedIssueId = :issueId "
        + "GROUP BY r.id "
        + "ORDER BY COUNT(l) DESC, r.id DESC")
    List<ReprocessedIssueOpinion> findTop5ByActiveLikes(Pageable pageable, Long issueId);
}

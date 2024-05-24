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
        + "WHERE r.reprocessedIssueId = :issueId AND r.isReliable in :reliabilities "
        + "ORDER BY r.createdAt DESC")
    List<ReprocessedIssueOpinion> findByIssueIdAndIsReliableWithLikes(final Long issueId,
                                                                      final List<Boolean> reliabilities,
                                                                      final Pageable pageable);

    @Query("SELECT r "
        + "FROM ReprocessedIssueOpinion r "
        + "LEFT JOIN r.likes l on r.id = l.reprocessedIssueOpinionId "
        + "WHERE l.isDeleted = false AND r.reprocessedIssueId = :issueId AND r.isReliable in :reliabilities "
        + "GROUP BY r.id "
        + "ORDER BY COUNT(l) DESC, r.id DESC")
    List<ReprocessedIssueOpinion> findOpinionsOrderByLike(final Long issueId,
                                                        final List<Boolean> reliabilities,
                                                        final Pageable pageable);

    @Query("SELECT r "
        + "FROM ReprocessedIssueOpinion r "
        + "LEFT JOIN r.likes l "
        + "WHERE l.isDeleted = false AND r.reprocessedIssueId = :issueId "
        + "GROUP BY r.id "
        + "ORDER BY COUNT(l) DESC, r.id DESC")
    List<ReprocessedIssueOpinion> findTop5ByActiveLikes(Pageable pageable, Long issueId);

    @Query("SELECT r "
        + "FROM ReprocessedIssueOpinion r "
        + "LEFT JOIN r.likes l on r.id = l.reprocessedIssueOpinionId "
        + "WHERE r.paragraphId = :paragraphId AND r.isReliable in :reliability AND l.isDeleted = false "
        + "GROUP BY r.id "
        + "ORDER BY COUNT(l) DESC, r.id DESC"
    )
    List<ReprocessedIssueOpinion> findTop5ByParagraphIdOrderByLikes(final Long paragraphId,
                                                                    final List<Boolean> reliability,
                                                                    final Pageable pageable);

    @Query("SELECT r "
        + "FROM ReprocessedIssueOpinion r "
        + "WHERE r.paragraphId = :paragraphId AND r.isReliable in :reliability "
        + "ORDER BY r.createdAt DESC"
    )
    List<ReprocessedIssueOpinion> findTop5ByParagraphIdOrderByCreatedAt(final Long paragraphId,
                                                                        final List<Boolean> reliability,
                                                                        final Pageable pageable);
}

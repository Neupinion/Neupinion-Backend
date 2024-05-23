package com.neupinion.neupinion.opinion.domain.repository;

import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinionLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReprocessedIssueOpinionLikeRepository extends JpaRepository<ReprocessedIssueOpinionLike, Long> {

    boolean existsByMemberIdAndReprocessedIssueOpinionIdAndIsDeletedFalse(final Long memberId,
                                                                          final Long reprocessedIssueOpinionId);

    Optional<ReprocessedIssueOpinionLike> findByMemberIdAndReprocessedIssueOpinionId(final Long memberId,
                                                                                     final Long reprocessedIssueOpinionId);
}

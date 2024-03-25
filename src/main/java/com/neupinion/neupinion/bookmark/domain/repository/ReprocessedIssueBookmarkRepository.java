package com.neupinion.neupinion.bookmark.domain.repository;

import com.neupinion.neupinion.bookmark.domain.ReprocessedIssueBookmark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReprocessedIssueBookmarkRepository extends JpaRepository<ReprocessedIssueBookmark, Long> {

    Optional<ReprocessedIssueBookmark> findByReprocessedIssueIdAndMemberId(final Long id, final Long memberId);

    boolean existsByReprocessedIssueIdAndMemberIdAndIsBookmarkedIsTrue(final Long id, final Long memberId);
}

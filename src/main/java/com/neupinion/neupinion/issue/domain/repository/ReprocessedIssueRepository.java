package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReprocessedIssueRepository extends JpaRepository<ReprocessedIssue, Long> {

    @Query("SELECT ri "
        + "FROM ReprocessedIssue ri "
        + "WHERE FUNCTION('DATE', ri.createdAt) = :createdAt "
        + "ORDER BY ri.createdAt DESC")
    List<ReprocessedIssue> findByCreatedAt(LocalDateTime createdAt, Pageable pageable);
}

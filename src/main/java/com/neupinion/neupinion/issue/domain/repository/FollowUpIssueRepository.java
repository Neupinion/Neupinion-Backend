package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.repository.dto.FollowUpIssueWithReprocessedIssueTitle;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FollowUpIssueRepository extends JpaRepository<FollowUpIssue, Long> {

    @Query(value = "SELECT fui as followUpIssue, ri.title as reprocessedIssueTitle "
        + "FROM FollowUpIssue fui "
        + "LEFT JOIN ReprocessedIssue ri ON fui.reprocessedIssueId = ri.id "
        + "WHERE CAST(fui.createdAt AS DATE) = :createdAt AND fui.category = :category"
    )
    List<FollowUpIssueWithReprocessedIssueTitle> findByCategoryAndDate(final Category category, final LocalDate createdAt);
}

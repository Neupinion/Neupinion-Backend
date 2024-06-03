package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.IssueStand;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueStandRepository extends JpaRepository<IssueStand, Long> {

    List<IssueStand> findByIssueIdOrderById(Long issueId);
}

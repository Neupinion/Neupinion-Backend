package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Long> {

}

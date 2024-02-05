package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.IssueComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueCommentRepository extends JpaRepository<IssueComment, Long> {

}

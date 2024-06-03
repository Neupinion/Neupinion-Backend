package com.neupinion.neupinion.article.domain.repository;

import com.neupinion.neupinion.article.domain.IssueKeyword;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueKeywordRepository extends JpaRepository<IssueKeyword, Long> {

    List<IssueKeyword> findByIssueId(Long issueId);
}

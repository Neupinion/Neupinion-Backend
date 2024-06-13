package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.IssueStandReference;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueStandReferenceRepository extends JpaRepository<IssueStandReference, Long> {

    List<IssueStandReference> findByIssueStandIdIn(List<Long> standIds);
}

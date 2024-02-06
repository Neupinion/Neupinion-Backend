package com.neupinion.neupinion.issue.domain.repository;

import com.neupinion.neupinion.issue.domain.Opinion;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {

    Set<Opinion> findByMemberId(final Long memberId);
}

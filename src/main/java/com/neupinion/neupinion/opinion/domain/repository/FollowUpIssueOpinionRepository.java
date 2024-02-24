package com.neupinion.neupinion.opinion.domain.repository;

import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowUpIssueOpinionRepository extends JpaRepository<FollowUpIssueOpinion, Long> {

    Set<FollowUpIssueOpinion> findByMemberId(final Long memberId);

    boolean existsByMemberIdAndParagraphId(final Long memberId, final Long paragraphId);
}

package com.neupinion.neupinion.issue.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reprocessed_issue_trust_vote")
@Entity
public class ReprocessedIssueTrustVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reprocessed_issue_id", nullable = false, updatable = false)
    private Long reprocessedIssueId;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Embedded
    private RelatableStand relatableStand;

    private ReprocessedIssueTrustVote(final Long id, final Long reprocessedIssueId, final Long memberId,
                                     final RelatableStand relatableStand) {
        this.id = id;
        this.reprocessedIssueId = reprocessedIssueId;
        this.memberId = memberId;
        this.relatableStand = relatableStand;
    }

    public static ReprocessedIssueTrustVote forSave(final Long reprocessedIssueId, final Long memberId, final RelatableStand relatableStand) {
        return new ReprocessedIssueTrustVote(null, reprocessedIssueId, memberId, relatableStand);
    }

    public void updateSelectedStand(final Long firstStandId, final boolean firstRelatable, final Long secondStandId, final boolean secondRelatable) {
        this.relatableStand = new RelatableStand(firstStandId, firstRelatable, secondStandId, secondRelatable);
    }
}

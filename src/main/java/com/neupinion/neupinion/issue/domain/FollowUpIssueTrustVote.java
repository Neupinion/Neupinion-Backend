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
@Table(name = "follow_up_issue_trust_vote")
@Entity
public class FollowUpIssueTrustVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "follow_up_issue_id", nullable = false, updatable = false)
    private Long followUpIssueId;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Embedded
    private RelatableStand relatableStand;

    private FollowUpIssueTrustVote(final Long id, final Long followUpIssueId, final Long memberId,
                                  final RelatableStand relatableStand) {
        this.id = id;
        this.followUpIssueId = followUpIssueId;
        this.memberId = memberId;
        this.relatableStand = relatableStand;
    }

    public static FollowUpIssueTrustVote forSave(final Long followUpIssueId, final Long memberId, final RelatableStand relatableStand) {
        return new FollowUpIssueTrustVote(null, followUpIssueId, memberId, relatableStand);
    }
}

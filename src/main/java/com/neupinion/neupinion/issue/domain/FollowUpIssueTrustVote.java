package com.neupinion.neupinion.issue.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VoteStatus status;

    private FollowUpIssueTrustVote(final Long id, final Long followUpIssueId, final Long memberId,
                                  final VoteStatus status) {
        this.id = id;
        this.followUpIssueId = followUpIssueId;
        this.memberId = memberId;
        this.status = status;
    }

    public static FollowUpIssueTrustVote forSave(final Long followUpIssueId, final Long memberId, final VoteStatus status) {
        return new FollowUpIssueTrustVote(null, followUpIssueId, memberId, status);
    }
}

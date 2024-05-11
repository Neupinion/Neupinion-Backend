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

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VoteStatus status;

    private ReprocessedIssueTrustVote(final Long id, final Long reprocessedIssueId, final Long memberId, final String status) {
        this.id = id;
        this.reprocessedIssueId = reprocessedIssueId;
        this.memberId = memberId;
        this.status = VoteStatus.from(status);
    }

    private ReprocessedIssueTrustVote(final Long id, final Long reprocessedIssueId, final Long memberId,
                                     final VoteStatus status) {
        this.id = id;
        this.reprocessedIssueId = reprocessedIssueId;
        this.memberId = memberId;
        this.status = status;
    }

    public static ReprocessedIssueTrustVote forSave(final Long reprocessedIssueId, final Long memberId, final String status) {
        return new ReprocessedIssueTrustVote(null, reprocessedIssueId, memberId, status);
    }

    public static ReprocessedIssueTrustVote forSave(final Long reprocessedIssueId, final Long memberId, final VoteStatus status) {
        return new ReprocessedIssueTrustVote(null, reprocessedIssueId, memberId, status);
    }

    public void updateStatus(final String status) {
        this.status = VoteStatus.from(status);
    }
}

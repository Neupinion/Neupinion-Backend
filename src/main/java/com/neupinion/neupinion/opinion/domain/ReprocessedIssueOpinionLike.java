package com.neupinion.neupinion.opinion.domain;

import jakarta.persistence.Column;
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
@Table(name = "reprocessed_issue_opinion_like")
@Entity
public class ReprocessedIssueOpinionLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "reprocessed_issue_opinion_id")
    private Long reprocessedIssueOpinionId;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    private ReprocessedIssueOpinionLike(final Long id, final Long memberId, final Long reprocessedIssueOpinionId) {
        this.id = id;
        this.memberId = memberId;
        this.reprocessedIssueOpinionId = reprocessedIssueOpinionId;
        this.isDeleted = false;
    }

    public static ReprocessedIssueOpinionLike forSave(final Long memberId, final Long reprocessedIssueOpinionId) {
        return new ReprocessedIssueOpinionLike(null, memberId, reprocessedIssueOpinionId);
    }

    public void updateDeletionStatus() {
        this.isDeleted = !isDeleted;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }
}

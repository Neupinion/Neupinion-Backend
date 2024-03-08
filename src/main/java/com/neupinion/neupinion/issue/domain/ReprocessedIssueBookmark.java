package com.neupinion.neupinion.issue.domain;

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
@Table(name = "reprocessed_issue_bookmark")
@Entity
public class ReprocessedIssueBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reprocessed_issue_id", nullable = false, updatable = false)
    private Long reprocessedIssueId;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    private ReprocessedIssueBookmark(final Long id, final Long reprocessedIssueId, final Long memberId, final boolean isDeleted) {
        this.id = id;
        this.reprocessedIssueId = reprocessedIssueId;
        this.memberId = memberId;
        this.isDeleted = isDeleted;
    }

    public static ReprocessedIssueBookmark forSave(final Long reprocessedIssueId, final Long memberId, final boolean isDeleted) {
        return new ReprocessedIssueBookmark(null, reprocessedIssueId, memberId, isDeleted);
    }

    public void updateDeleteStatus() {
        this.isDeleted = !this.isDeleted;
    }
}

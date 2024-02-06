package com.neupinion.neupinion.issue.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "issue_comment")
@Entity
public class IssueComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private IssueCommentContent content;

    @Column(name = "reprocessed_issue_id", nullable = false, updatable = false)
    private Long reprocessedIssueId;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    private IssueComment(final Long id, final Long reprocessedIssueId, final Long memberId, final String content) {
        this.id = id;
        this.reprocessedIssueId = reprocessedIssueId;
        this.memberId = memberId;
        this.content = new IssueCommentContent(content);
    }

    public static IssueComment forSave(final Long reprocessedIssueId, final Long memberId, final String content) {
        return new IssueComment(null, reprocessedIssueId, memberId, content);
    }

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IssueComment issueComment = (IssueComment) o;
        if (Objects.isNull(issueComment.id) || Objects.isNull(this.id)) {
            return false;
        }
        return Objects.equals(id, issueComment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package com.neupinion.neupinion.issue.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "follow_up_issue_views")
@Entity
public class FollowUpIssueViews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "follow_up_issue_id", nullable = false, updatable = false)
    private Long followUpIssueId;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    private FollowUpIssueViews(final Long id, final Long followUpIssueId, final Long memberId) {
        this.id = id;
        this.followUpIssueId = followUpIssueId;
        this.memberId = memberId;
    }

    public static FollowUpIssueViews of(final Long followUpIssueId, final Long memberId) {
        return new FollowUpIssueViews(null, followUpIssueId, memberId);
    }

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}

package com.neupinion.neupinion.issue.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "follow_up_issue")
@Entity
public class FollowUpIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private IssueTitle title;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "tag", nullable = false)
    @Enumerated(EnumType.STRING)
    private FollowUpIssueTag tag;

    @Column(name = "reprocessed_issue_id", nullable = false, updatable = false)
    private Long reprocessedIssueId;

    @Transient
    private Clock clock = Clock.systemDefaultZone();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);

    private FollowUpIssue(final Long id, final String title, final String imageUrl, final Category category,
                          final FollowUpIssueTag tag, final Long reprocessedIssueId, final Clock clock) {
        this.id = id;
        this.title = new IssueTitle(title);
        this.imageUrl = imageUrl;
        this.category = category;
        this.tag = tag;
        this.reprocessedIssueId = reprocessedIssueId;
        this.clock = clock;
    }

    public static FollowUpIssue forSave(final String title, final String imageUrl, final Category category,
                                        final FollowUpIssueTag tag, final Long reprocessedIssueId) {
        return new FollowUpIssue(null, title, imageUrl, category, tag, reprocessedIssueId, Clock.systemDefaultZone());
    }

    public static FollowUpIssue forSave(final String title, final String imageUrl, final Category category,
                                        final FollowUpIssueTag tag, final Long reprocessedIssueId, final Clock clock) {
        return new FollowUpIssue(null, title, imageUrl, category, tag, reprocessedIssueId, clock);
    }

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);
    }
}

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
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "reprocessed_issue")
@Entity
public class ReprocessedIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private IssueTitle title;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "issue_id", unique = true)
    private Long issueId;

    @Transient
    private Clock clock = Clock.systemDefaultZone();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);

    private ReprocessedIssue(final Long id, final String title, final String imageUrl, final Category category,
                             final Long issueId, final Clock clock) {
        this.id = id;
        this.title = new IssueTitle(title);
        this.imageUrl = imageUrl;
        this.category = category;
        this.issueId = issueId;
        this.createdAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);
        this.updatedAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);
    }

    public static ReprocessedIssue forSave(final String title, final String imageUrl, final Category category,
                                           final Long issueId, final Clock clock) {
        return new ReprocessedIssue(null, title, imageUrl, category, issueId, clock);
    }

    public static ReprocessedIssue forSave(final String title, final String imageUrl, final Category category,
                                           final Long issueId) {
        return new ReprocessedIssue(null, title, imageUrl, category, issueId, Clock.systemDefaultZone());
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
        final ReprocessedIssue reprocessedIssue = (ReprocessedIssue) o;
        if (Objects.isNull(reprocessedIssue.id) || Objects.isNull(this.id)) {
            return false;
        }
        return Objects.equals(id, reprocessedIssue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

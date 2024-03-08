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

    private static final int VIEWS_INITIALIZATION = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private IssueTitle title;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "caption")
    private String caption;

    @Column(name = "origin_url", nullable = false)
    private String originUrl;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "views", nullable = false)
    private int views;

    @Transient
    private Clock clock = Clock.systemDefaultZone();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);

    private ReprocessedIssue(final Long id, final String title, final String imageUrl, final String caption,
                             final String originUrl, final Category category, final int views, final Clock clock) {
        this.id = id;
        this.title = new IssueTitle(title);
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.originUrl = originUrl;
        this.category = category;
        this.views = views;
        this.clock = clock;
    }

    public static ReprocessedIssue forSave(final String title, final String imageUrl, final String caption,
                                           final String originUrl, final Category category, final Clock clock) {
        return new ReprocessedIssue(null, title, imageUrl, caption, originUrl, category, VIEWS_INITIALIZATION, clock);
    }

    public static ReprocessedIssue forSave(final String title, final String imageUrl, final String caption,
                                           final String originUrl, final Category category) {
        return new ReprocessedIssue(null, title, imageUrl, caption, originUrl, category, VIEWS_INITIALIZATION,
                                    Clock.systemDefaultZone());
    }

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);
    }

    public String getTitle() {
        return title.getValue();
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

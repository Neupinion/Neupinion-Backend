package com.neupinion.neupinion.issue.domain;

import com.neupinion.neupinion.opinion.domain.repository.dto.IssueCommentMapping;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@SqlResultSetMapping(
    name = "IssueCommentMapping",
    classes = @ConstructorResult(
        targetClass = IssueCommentMapping.class,
        columns = {
            @ColumnResult(name = "id", type = Long.class),
            @ColumnResult(name = "paragraphId", type = Long.class),
            @ColumnResult(name = "issueId", type = Long.class),
            @ColumnResult(name = "writerId", type = Long.class),
            @ColumnResult(name = "content", type = String.class),
            @ColumnResult(name = "isReliable", type = Boolean.class),
            @ColumnResult(name = "issueType", type = String.class),
            @ColumnResult(name = "createdAt", type = LocalDateTime.class),
        }
    )
)
@NamedNativeQuery(
    name = "ReprocessedIssue.findAllCommentsOrderByCreatedAtDesc",
    query =
        "SELECT rio.id AS id, rio.paragraph_id AS paragraphId, rio.reprocessed_issue_id AS issueId, rio.member_id AS writerId, rio.content AS content, rio.is_reliable AS isReliable, 'REPROCESSED' AS issueType, rio.created_at AS createdAt "
            + "FROM reprocessed_issue_opinion rio "
            + "WHERE rio.reprocessed_issue_id = :issueId "
            + "UNION ALL "
            + "SELECT fuo.id AS id, fuo.paragraph_id AS paragraphId, fuo.follow_up_issue_id AS issueId, fuo.member_id AS writerId, fuo.content AS content, fuo.is_reliable AS isReliable, 'FOLLOW_UP' AS issueType, fuo.created_at AS createdAt "
            + "FROM follow_up_issue_opinion fuo "
            + "WHERE fuo.follow_up_issue_id IN :followUpIssueIds "
            + "ORDER BY createdAt DESC",
    resultSetMapping = "IssueCommentMapping"
)
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

    @Column(name = "topic", nullable = false)
    private String topic;

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
                             final String originUrl, final Category category, final int views, final Clock clock,
                             final String topic) {
        this.id = id;
        this.title = new IssueTitle(title);
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.originUrl = originUrl;
        this.category = category;
        this.views = views;
        this.topic = topic;
        this.clock = clock;
    }

    public static ReprocessedIssue forSave(final String title, final String imageUrl, final String caption,
                                           final String originUrl, final Category category, final String topic,
                                           final Clock clock) {
        return new ReprocessedIssue(null, title, imageUrl, caption, originUrl, category, VIEWS_INITIALIZATION, clock,
                                    topic);
    }

    public static ReprocessedIssue forSave(final String title, final String imageUrl, final String caption,
                                           final String originUrl, final Category category, final String topic) {
        return new ReprocessedIssue(null, title, imageUrl, caption, originUrl, category, VIEWS_INITIALIZATION,
                                    Clock.systemDefaultZone(), topic);
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

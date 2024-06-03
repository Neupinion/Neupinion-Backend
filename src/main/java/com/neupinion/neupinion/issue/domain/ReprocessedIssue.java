package com.neupinion.neupinion.issue.domain;

import com.neupinion.neupinion.opinion.domain.repository.dto.IssueOpinionMapping;
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
    name = "IssueOpinionMapping",
    classes = @ConstructorResult(
        targetClass = IssueOpinionMapping.class,
        columns = {
            @ColumnResult(name = "id", type = Long.class),
            @ColumnResult(name = "paragraphId", type = Long.class),
            @ColumnResult(name = "issueId", type = Long.class),
            @ColumnResult(name = "writerId", type = Long.class),
            @ColumnResult(name = "content", type = String.class),
            @ColumnResult(name = "isReliable", type = Boolean.class),
            @ColumnResult(name = "issueType", type = String.class),
            @ColumnResult(name = "createdAt", type = LocalDateTime.class),
            @ColumnResult(name = "likeCount", type = Integer.class)
        }
    )
)
@NamedNativeQuery(
    name = "ReprocessedIssue.findAllOpinionsOrderByCreatedAtDesc",
    query =
        "SELECT rio.id AS id, rio.paragraph_id AS paragraphId, rio.reprocessed_issue_id AS issueId, rio.member_id AS writerId, rio.content AS content, rio.is_reliable AS isReliable, 'REPROCESSED' AS issueType, rio.created_at AS createdAt, COUNT(riol.id) AS likeCount "
            + "FROM reprocessed_issue_opinion rio "
            + "LEFT JOIN reprocessed_issue_opinion_like riol on rio.id = riol.reprocessed_issue_opinion_id "
            + "WHERE rio.reprocessed_issue_id = :issueId AND rio.is_reliable IN :reliabilities "
            + "GROUP BY rio.id, rio.id, rio.paragraph_id, rio.reprocessed_issue_id, rio.member_id, rio.content, rio.is_reliable, 'REPROCESSED', rio.created_at "
            + "UNION ALL "
            + "SELECT fuo.id AS id, fuo.paragraph_id AS paragraphId, fuo.follow_up_issue_id AS issueId, fuo.member_id AS writerId, fuo.content AS content, fuo.is_reliable AS isReliable, 'FOLLOW_UP' AS issueType, fuo.created_at AS createdAt, COUNT(fiol.id) AS likeCount "
            + "FROM follow_up_issue_opinion fuo "
            + "LEFT JOIN follow_up_issue_opinion_like fiol on fuo.id = fiol.follow_up_issue_opinion_id "
            + "WHERE fuo.follow_up_issue_id IN :followUpIssueIds AND fuo.is_reliable IN :reliabilities "
            + "GROUP BY fuo.id, fuo.paragraph_id, fuo.follow_up_issue_id, fuo.member_id, fuo.content, fuo.is_reliable, fuo.created_at "
            + "ORDER BY createdAt DESC",
    resultSetMapping = "IssueOpinionMapping"
)
@NamedNativeQuery(
    name = "ReprocessedIssue.findAllOpinionsOrderByLikesAtDesc",
    query =
        "SELECT rio.id AS id, rio.paragraph_id AS paragraphId, rio.reprocessed_issue_id AS issueId, rio.member_id AS writerId, rio.content AS content, rio.is_reliable AS isReliable, 'REPROCESSED' AS issueType, rio.created_at AS createdAt, COUNT(riol.id) AS likeCount "
            + "FROM reprocessed_issue_opinion rio "
            + "LEFT JOIN reprocessed_issue_opinion_like riol on rio.id = riol.reprocessed_issue_opinion_id "
            + "WHERE rio.reprocessed_issue_id = :issueId AND rio.is_reliable IN :reliabilities "
            + "GROUP BY rio.id, rio.id, rio.paragraph_id, rio.reprocessed_issue_id, rio.member_id, rio.content, rio.is_reliable, 'REPROCESSED', rio.created_at "
            + "UNION ALL "
            + "SELECT fuo.id AS id, fuo.paragraph_id AS paragraphId, fuo.follow_up_issue_id AS issueId, fuo.member_id AS writerId, fuo.content AS content, fuo.is_reliable AS isReliable, 'FOLLOW_UP' AS issueType, fuo.created_at AS createdAt, COUNT(fiol.id) AS likeCount "
            + "FROM follow_up_issue_opinion fuo "
            + "LEFT JOIN follow_up_issue_opinion_like fiol on fuo.id = fiol.follow_up_issue_opinion_id "
            + "WHERE fuo.follow_up_issue_id IN :followUpIssueIds AND fuo.is_reliable IN :reliabilities "
            + "GROUP BY fuo.id, fuo.paragraph_id, fuo.follow_up_issue_id, fuo.member_id, fuo.content, fuo.is_reliable, fuo.created_at "
            + "ORDER BY likeCount DESC, createdAt DESC",
    resultSetMapping = "IssueOpinionMapping"
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

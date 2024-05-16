package com.neupinion.neupinion.opinion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reprocessed_issue_opinion")
@Entity
public class ReprocessedIssueOpinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private OpinionContent content;

    @Column(name = "paragraph_id", nullable = false, updatable = false)
    private Long paragraphId;

    @Column(name = "reprocessed_issue_id", nullable = false, updatable = false)
    private Long reprocessedIssueId;

    @Column(name = "is_reliable", nullable = false)
    private boolean isReliable;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @OneToMany
    @SQLRestriction("is_deleted = false")
    @JoinColumn(name = "reprocessed_issue_opinion_id")
    List<ReprocessedIssueOpinionLike> likes = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    private ReprocessedIssueOpinion(final Long id, final Long paragraphId, final Long reprocessedIssueId,
                                    final boolean isReliable,
                                    final Long memberId, final String content) {
        this.id = id;
        this.reprocessedIssueId = reprocessedIssueId;
        this.memberId = memberId;
        this.isReliable = isReliable;
        this.content = new OpinionContent(content);
        this.paragraphId = paragraphId;
    }

    public static ReprocessedIssueOpinion forSave(final Long paragraphId, final Long reprocessedIssueId,
                                                  final boolean isReliable,
                                                  final Long memberId, final String content) {
        return new ReprocessedIssueOpinion(null, paragraphId, reprocessedIssueId, isReliable, memberId, content);
    }

    public boolean getIsReliable() {
        return isReliable;
    }

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    public void update(final Long paragraphId, final String content, final boolean isReliable) {
        this.paragraphId = paragraphId;
        this.content = new OpinionContent(content);
        this.isReliable = isReliable;
    }

    public String getContent() {
        return content.getValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReprocessedIssueOpinion reprocessedIssueOpinion = (ReprocessedIssueOpinion) o;
        if (Objects.isNull(reprocessedIssueOpinion.id) || Objects.isNull(this.id)) {
            return false;
        }
        return Objects.equals(id, reprocessedIssueOpinion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

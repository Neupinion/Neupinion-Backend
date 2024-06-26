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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "follow_up_issue_opinion")
@Entity
public class FollowUpIssueOpinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paragraph_id", nullable = false, updatable = false)
    private Long paragraphId;

    @Column(name = "follow_up_issue_id", nullable = false, updatable = false)
    private Long followUpIssueId;

    @Column(name = "is_reliable", nullable = false)
    private boolean isReliable;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @OneToMany
    @JoinColumn(name = "follow_up_issue_opinion_id")
    List<FollowUpIssueOpinionLike> likes = new ArrayList<>();

    @Embedded
    private OpinionContent content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    private FollowUpIssueOpinion(final Long id, Long paragraphId, final Long followUpIssueId, final boolean isReliable,
                                 final Long memberId, String content) {
        this.id = id;
        this.paragraphId = paragraphId;
        this.followUpIssueId = followUpIssueId;
        this.isReliable = isReliable;
        this.memberId = memberId;
        this.content = new OpinionContent(content);
    }

    public static FollowUpIssueOpinion forSave(final Long paragraphId, final Long followUpIssueId,
                                               final boolean isReliable, final Long memberId, final String content) {
        return new FollowUpIssueOpinion(null, paragraphId, followUpIssueId, isReliable, memberId, content);
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

    public boolean getIsReliable() {
        return isReliable;
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
        final FollowUpIssueOpinion followUpIssueOpinion = (FollowUpIssueOpinion) o;
        if (Objects.isNull(this.id) || Objects.isNull(followUpIssueOpinion.id)) {
            return false;
        }
        return Objects.equals(id, followUpIssueOpinion.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

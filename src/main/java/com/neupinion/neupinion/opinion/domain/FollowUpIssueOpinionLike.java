package com.neupinion.neupinion.opinion.domain;

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
@Table(name = "follow_up_issue_opinion_like")
@Entity
public class FollowUpIssueOpinionLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "follow_up_issue_opinion_id")
    private Long followUpIssueOpinionId;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    private FollowUpIssueOpinionLike(final Long id, final Long memberId, final Long followUpIssueOpinionId) {
        this.id = id;
        this.memberId = memberId;
        this.followUpIssueOpinionId = followUpIssueOpinionId;
        this.isDeleted = false;
    }

    public static FollowUpIssueOpinionLike forSave(final Long memberId, final Long followUpIssueId) {
        return new FollowUpIssueOpinionLike(null, memberId, followUpIssueId);
    }

    public void updateDeletionStatus() {
        this.isDeleted = !isDeleted;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }
}

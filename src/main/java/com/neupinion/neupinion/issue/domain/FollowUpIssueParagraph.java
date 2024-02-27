package com.neupinion.neupinion.issue.domain;

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
@Table(name = "follow_up_issue_paragraph")
@Entity
public class FollowUpIssueParagraph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "selectable")
    private boolean selectable;

    @Column(name = "follow_up_issue_id", nullable = false, updatable = false)
    private Long followUpIssueId;

    private FollowUpIssueParagraph(final Long id, final String content, final boolean selectable, final Long followUpIssueId) {
        this.id = id;
        this.content = content;
        this.selectable = selectable;
        this.followUpIssueId = followUpIssueId;
    }

    public static FollowUpIssueParagraph forSave(final String content, final boolean selectable, final Long followUpIssueId) {
        return new FollowUpIssueParagraph(null, content, selectable, followUpIssueId);
    }
}

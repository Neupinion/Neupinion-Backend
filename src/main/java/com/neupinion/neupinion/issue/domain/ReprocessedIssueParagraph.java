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
@Table(name = "reprocessed_issue_paragraph")
@Entity
public class ReprocessedIssueParagraph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "selectable")
    private boolean selectable;

    @Column(name = "reprocessed_issue_id", nullable = false, updatable = false)
    private Long reprocessedIssueId;

    private ReprocessedIssueParagraph(final Long id, final String content, final boolean selectable, final Long reprocessedIssueId) {
        this.id = id;
        this.content = content;
        this.selectable = selectable;
        this.reprocessedIssueId = reprocessedIssueId;
    }

    public static ReprocessedIssueParagraph forSave(final String content, final boolean selectable, final Long reprocessedIssueId) {
        return new ReprocessedIssueParagraph(null, content, selectable, reprocessedIssueId);
    }
}

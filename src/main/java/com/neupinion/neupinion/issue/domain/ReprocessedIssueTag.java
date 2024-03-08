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
@Table(name = "reprocessed_issue_tag")
@Entity
public class ReprocessedIssueTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reprocessed_issue_id", nullable = false, updatable = false)
    private Long reprocessedIssueId;

    @Column(name = "tag", nullable = false)
    private String tag;

    private ReprocessedIssueTag(final Long id, final Long reprocessedIssueId, final String tag) {
        this.id = id;
        this.reprocessedIssueId = reprocessedIssueId;
        this.tag = tag;
    }

    public static ReprocessedIssueTag forSave(final Long reprocessedIssueId, final String tag) {
        return new ReprocessedIssueTag(null, reprocessedIssueId, tag);
    }
}

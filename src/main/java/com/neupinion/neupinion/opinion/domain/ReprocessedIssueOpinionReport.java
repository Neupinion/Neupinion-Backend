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
@Table(name = "reprocessed_issue_opinion_report")
@Entity
public class ReprocessedIssueOpinionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reprocessed_issue_opinion_id", nullable = false)
    private Long opinionId;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "content", length = 1000)
    private String content;

    private ReprocessedIssueOpinionReport(final Long id, final Long opinionId, final Long reporterId,
                                          final String reason, final String content) {
        this.id = id;
        this.opinionId = opinionId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.content = content;
    }

    public static ReprocessedIssueOpinionReport forSave(final Long opinionId, final Long reporterId,
                                                        final String reason, final String content) {
        return new ReprocessedIssueOpinionReport(null, opinionId, reporterId, reason, content);
    }
}

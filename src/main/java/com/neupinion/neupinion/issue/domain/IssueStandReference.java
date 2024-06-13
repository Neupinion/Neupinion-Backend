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
@Table(name = "issue_stand_reference")
@Entity
public class IssueStandReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issue_stand_id", nullable = false)
    private Long issueStandId;

    @Column(name = "reference_url", nullable = false, length = 10000)
    private String referenceUrl;

    private IssueStandReference(final Long id, final Long issueStandId, final String referenceUrl) {
        this.id = id;
        this.issueStandId = issueStandId;
        this.referenceUrl = referenceUrl;
    }

    public static IssueStandReference forSave(final Long issueStandId, final String referenceUrl) {
        return new IssueStandReference(null, issueStandId, referenceUrl);
    }
}

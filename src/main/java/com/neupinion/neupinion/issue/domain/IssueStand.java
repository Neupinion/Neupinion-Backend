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
@Table(name = "issue_stand")
@Entity
public class IssueStand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stand", nullable = false)
    private String stand;

    @Column(name = "issue_id", nullable = false)
    private Long issueId;

    private IssueStand(final Long id, final String stand, final Long issueId) {
        this.id = id;
        this.stand = stand;
        this.issueId = issueId;
    }

    public static IssueStand forSave(final String stand, final Long issueId) {
        return new IssueStand(null, stand, issueId);
    }
}

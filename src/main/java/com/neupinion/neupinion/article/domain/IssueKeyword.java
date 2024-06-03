package com.neupinion.neupinion.article.domain;

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
@Table(name = "issue_keyword")
@Entity
public class IssueKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "issue_stand_id", nullable = false)
    private Long issueStandId;

    @Column(name = "issue_id", nullable = false)
    private Long issueId;

    private IssueKeyword(final Long id, final String keyword, final Long issueStandId, final Long issueId) {
        this.id = id;
        this.keyword = keyword;
        this.issueStandId = issueStandId;
        this.issueId = issueId;
    }

    public static IssueKeyword forSave(final String keyword, final Long issueStandId, final Long issueId) {
        return new IssueKeyword(null, keyword, issueStandId, issueId);
    }
}

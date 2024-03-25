package com.neupinion.neupinion.bookmark.domain;

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
@Table(name = "reprocessed_issue_bookmark")
@Entity
public class ReprocessedIssueBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reprocessed_issue_id")
    private Long reprocessedIssueId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "is_bookmarked")
    private boolean isBookmarked;

    private ReprocessedIssueBookmark(final Long id, final Long reprocessedIssueId, final Long memberId,
                                    final boolean isBookmarked) {
        this.id = id;
        this.reprocessedIssueId = reprocessedIssueId;
        this.memberId = memberId;
        this.isBookmarked = isBookmarked;
    }

    public static ReprocessedIssueBookmark forSave(final Long reprocessedIssueId, final Long memberId) {
        return new ReprocessedIssueBookmark(null, reprocessedIssueId, memberId, true);
    }

    public void updateIsBookmarked(final boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }

    public boolean getIsBookmarked() {
        return isBookmarked;
    }
}

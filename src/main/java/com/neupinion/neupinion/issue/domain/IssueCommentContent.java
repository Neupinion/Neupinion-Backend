package com.neupinion.neupinion.issue.domain;

import com.neupinion.neupinion.issue.exception.IssueCommentException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class IssueCommentContent {

    private static final int CONTENT_LENGTH_MAXIMUM = 1000;

    @Column(name = "content", length = CONTENT_LENGTH_MAXIMUM, nullable = false)
    private String content;

    public IssueCommentContent(final String content) {
        validateContent(content);
        this.content = content;
    }

    private void validateContent(final String content) {
        if (content.length() > CONTENT_LENGTH_MAXIMUM) {
            throw new IssueCommentException.TooLongIssueCommentException();
        }
    }
}

package com.neupinion.neupinion.issue.domain;

import com.neupinion.neupinion.issue.exception.IssueException;
import com.neupinion.neupinion.utils.StringChecker;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Map;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
@Embeddable
public class IssueTitle {

    private static final int TITLE_MAXIMUM_LENGTH = 100;

    @Column(name = "title", length = TITLE_MAXIMUM_LENGTH, nullable = false)
    private String value;

    public IssueTitle(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (StringChecker.isNullOrBlank(value)) {
            throw new IssueException.NullOrEmptyTitleException();
        }
        if(value.length() > TITLE_MAXIMUM_LENGTH) {
            throw new IssueException.TooLongIssueTitleException(
                Map.of("title", value)
            );
        }
    }
}

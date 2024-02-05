package com.neupinion.neupinion.issue.domain;

import com.neupinion.neupinion.issue.exception.FollowUpIssueException.FollowUpIssueTagNotFoundException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum FollowUpIssueTag {

    TRIAL_RESULTS("재판 결과"),
    INTERVIEW("인터뷰"),
    OFFICIAL_POSITION("공식 입장"),
    ;

    private final String value;

    FollowUpIssueTag(final String value) {
        this.value = value;
    }

    public static FollowUpIssueTag from(final String value) {
        return Arrays.stream(values())
            .filter(tag -> tag.name().equals(value))
            .findFirst()
            .orElseThrow(FollowUpIssueTagNotFoundException::new);
    }
}

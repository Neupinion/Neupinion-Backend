package com.neupinion.neupinion.issue.domain;

import com.neupinion.neupinion.issue.exception.IssueException.IssueTypeNotFoundException;
import java.util.Arrays;
import java.util.Map;

public enum IssueType {

    REPROCESSED, FOLLOW_UP;

    public static IssueType from(final String value) {
        return Arrays.stream(IssueType.values())
            .filter(issueType -> issueType.name().equalsIgnoreCase(value))
            .findAny()
            .orElseThrow(() -> new IssueTypeNotFoundException(Map.of("wrongType", value)));
    }

    public static boolean isReprocessed(final String value) {
        return REPROCESSED.name().equalsIgnoreCase(value);
    }
}

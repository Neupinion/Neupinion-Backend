package com.neupinion.neupinion.issue.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class ReprocessedIssueException extends CustomException {

    protected ReprocessedIssueException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected ReprocessedIssueException(final ErrorCode errorCode, final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class ReprocessedIssueNotFoundException extends ReprocessedIssueException {

        public ReprocessedIssueNotFoundException() {
            super(ErrorCode.REPROCESSED_ISSUE_NOT_FOUND);
        }
    }
}

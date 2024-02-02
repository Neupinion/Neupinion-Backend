package com.neupinion.neupinion.issue.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class IssueException extends CustomException {

    protected IssueException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public IssueException(final ErrorCode errorCode, final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class EmptyReprocessedIssue extends IssueException {

        protected EmptyReprocessedIssue(final ErrorCode errorCode) {
            super(ErrorCode.EMPTY_REPROCESSED_ISSUE);
        }

        public EmptyReprocessedIssue(final ErrorCode errorCode, final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.EMPTY_REPROCESSED_ISSUE, inputValuesByProperty);
        }
    }
}

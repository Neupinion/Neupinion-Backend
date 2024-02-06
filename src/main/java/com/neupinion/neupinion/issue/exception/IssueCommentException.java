package com.neupinion.neupinion.issue.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class IssueCommentException extends CustomException {

    protected IssueCommentException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected IssueCommentException(final ErrorCode errorCode, final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class TooLongIssueCommentException extends IssueCommentException {

        public TooLongIssueCommentException() {
            super(ErrorCode.TOO_LONG_ISSUE_COMMENT_EXCEPTION);
        }
    }
}

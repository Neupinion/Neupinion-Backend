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

    public static class NullOrEmptyTitleException extends IssueException {

        public NullOrEmptyTitleException() {
            super(ErrorCode.NULL_OR_EMPTY_ISSUE_TITLE);
        }
    }

    public static class TooLongIssueTitleException extends IssueException {

        public TooLongIssueTitleException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.TOO_LONG_ISSUE_TITLE, inputValuesByProperty);
        }
    }

    public static class IssueTypeNotFoundException extends IssueException {

        public IssueTypeNotFoundException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.ISSUE_TYPE_NOT_FOUND, inputValuesByProperty);
        }
    }
}

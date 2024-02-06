package com.neupinion.neupinion.issue.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class FollowUpIssueException extends CustomException {

    protected FollowUpIssueException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected FollowUpIssueException(final ErrorCode errorCode, final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class FollowUpIssueTagNotFoundException extends FollowUpIssueException {

        public FollowUpIssueTagNotFoundException() {
            super(ErrorCode.FOLLOW_UP_ISSUE_TAG_NOT_FOUND);
        }
    }

    public static class InvalidViewModeException extends FollowUpIssueException {

        public InvalidViewModeException() {
            super(ErrorCode.INVALID_VIEW_MODE);
        }
    }
}

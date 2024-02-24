package com.neupinion.neupinion.issue.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class ParagraphException extends CustomException {

    public ParagraphException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public ParagraphException(final ErrorCode errorCode,
                              final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class ParagraphNotFoundException extends ParagraphException {

        public ParagraphNotFoundException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.PARAGRAPH_NOT_FOUND, inputValuesByProperty);
        }
    }

    public static class ParagraphForOtherIssueException extends ParagraphException {

        public ParagraphForOtherIssueException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.PARAGRAPH_FOR_OTHER_ISSUE, inputValuesByProperty);
        }
    }
}

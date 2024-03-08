package com.neupinion.neupinion.issue.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class VoteStatusException extends CustomException {

    public VoteStatusException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public VoteStatusException(final ErrorCode errorCode,
                               final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class VoteStatusNotFoundException extends VoteStatusException {
        public VoteStatusNotFoundException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.VOTE_STATUS_NOT_FOUND, inputValuesByProperty);
        }
    }
}

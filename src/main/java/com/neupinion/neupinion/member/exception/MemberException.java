package com.neupinion.neupinion.member.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class MemberException extends CustomException {

    public MemberException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public MemberException(final ErrorCode errorCode,
                           final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class MemberNotFoundException extends MemberException {

        public MemberNotFoundException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.MEMBER_NOT_FOUND, inputValuesByProperty);
        }
    }

    public static class NicknameLengthExceededException extends MemberException {

        public NicknameLengthExceededException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.NICKNAME_LENGTH_EXCEEDED, inputValuesByProperty);
        }
    }
}

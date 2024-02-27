package com.neupinion.neupinion.opinion.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class OpinionException extends CustomException {

    public OpinionException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public OpinionException(final ErrorCode errorCode,
                            final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class TooLongOpinionContentException extends OpinionException {

        public TooLongOpinionContentException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.TOO_LONG_OPINION_CONTENT, inputValuesByProperty);
        }
    }

    public static class EmptyOpinionContentException extends OpinionException {

        public EmptyOpinionContentException() {
            super(ErrorCode.EMPTY_OPINION_CONTENT);
        }
    }

    public static class AlreadyExistedOpinionException extends OpinionException {

        public AlreadyExistedOpinionException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.ALREADY_EXIST_OPINION, inputValuesByProperty);
        }
    }

    public static class NotFoundOpinionException extends OpinionException {

        public NotFoundOpinionException() {
            super(ErrorCode.NOT_FOUND_OPINION);
        }
    }

    public static class NotMatchedMemberException extends OpinionException {

        public NotMatchedMemberException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.NOT_MATCHED_MEMBER, inputValuesByProperty);
        }
    }
}

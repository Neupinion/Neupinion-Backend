package com.neupinion.neupinion.auth.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class TokenException extends CustomException {

    protected TokenException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected TokenException(final ErrorCode errorCode, final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class ExpiredTokenException extends TokenException {
        public ExpiredTokenException() {
            super(ErrorCode.EXPIRED_TOKEN);
        }
    }

    public static class NotIssuedTokenException extends TokenException {
        public NotIssuedTokenException() {
            super(ErrorCode.NOT_ISSUED_TOKEN);
        }
    }

    public static class RefreshTokenNotFoundException extends TokenException {
        public RefreshTokenNotFoundException(final Map<String, String> inputValues) {
            super(ErrorCode.NOT_FOUND_REFRESH_TOKEN, inputValues);
        }
    }

    public static class TokenPairNotMatchingException extends TokenException {
        public TokenPairNotMatchingException(final Map<String, String> inputValues) {
            super(ErrorCode.TOKEN_PAIR_NOT_MATCHING_EXCEPTION, inputValues);
        }
    }

    public static class AccessTokenNotFoundException extends TokenException {
        public AccessTokenNotFoundException(final Map<String, String> inputValues) {
            super(ErrorCode.NOT_FOUND_ACCESS_TOKEN, inputValues);
        }
    }
}

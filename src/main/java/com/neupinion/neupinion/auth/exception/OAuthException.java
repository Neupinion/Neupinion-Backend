package com.neupinion.neupinion.auth.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class OAuthException extends CustomException {


    protected OAuthException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected OAuthException(final ErrorCode errorCode, final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class NotFoundOAuthTypeException extends OAuthException {
        public NotFoundOAuthTypeException() {
            super(ErrorCode.OAUTH_TYPE_NOT_FOUND);
        }
    }

    public static class InvalidAccessTokenException extends OAuthException {
        public InvalidAccessTokenException(final Map<String, String> inputValues) {
            super(ErrorCode.INVALID_ACCESS_TOKEN, inputValues);
        }
    }

    public static class InvalidAuthorizationCodeException extends OAuthException {
        public InvalidAuthorizationCodeException(final Map<String, String> inputValues) {
            super(ErrorCode.INVALID_AUTHORIZATION_CODE, inputValues);
        }
    }

    public static class GoogleServerException extends OAuthException {
        public GoogleServerException(final Map<String, String> inputValues) {
            super(ErrorCode.GOOGLE_SERVER_EXCEPTION, inputValues);
        }
    }
}

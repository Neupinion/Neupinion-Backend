package com.neupinion.neupinion.article.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class ArticleException extends CustomException {

    public ArticleException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public ArticleException(final ErrorCode errorCode,
                            final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class UnsupportedStandException extends ArticleException {

        public UnsupportedStandException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.UNSUPPORTED_STAND, inputValuesByProperty);
        }
    }

    public static class OriginalLinkNotAccessibleException extends ArticleException {

        public OriginalLinkNotAccessibleException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.ORIGINAL_LINK_NOT_ACCESSIBLE, inputValuesByProperty);
        }
    }

    public static class NotFoundDomainException extends ArticleException {

        public NotFoundDomainException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.NOT_FOUND_DOMAIN, inputValuesByProperty);
        }
    }

    public static class UnorganizedResultException extends ArticleException {

        public UnorganizedResultException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.UNORGANIZED_RESULT, inputValuesByProperty);
        }
    }
}

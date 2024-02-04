package com.neupinion.neupinion.issue.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class CategoryException extends CustomException {

    protected CategoryException(final ErrorCode errorCode) {
        super(errorCode);
    }

    protected CategoryException(final ErrorCode errorCode, final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class CategoryNotFoundException extends CategoryException {

        public CategoryNotFoundException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.CATEGORY_NOT_FOUND_EXCEPTION, inputValuesByProperty);
        }

        public CategoryNotFoundException() {
            super(ErrorCode.CATEGORY_NOT_FOUND_EXCEPTION);
        }
    }
}

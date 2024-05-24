package com.neupinion.neupinion.image.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;
import java.util.Map;

public class ImageException extends CustomException {

    public ImageException(final ErrorCode errorCode) {
        super(errorCode);
    }

    public ImageException(final ErrorCode errorCode,
                          final Map<String, String> inputValuesByProperty) {
        super(errorCode, inputValuesByProperty);
    }

    public static class ImageProcessingException extends ImageException {
        public ImageProcessingException() {
            super(ErrorCode.IMAGE_READ_EXCEPTION);
        }
    }

    public static class ImageNotFoundException extends ImageException {
        public ImageNotFoundException(final Map<String, String> inputValuesByProperty) {
            super(ErrorCode.IMAGE_NOT_FOUND, inputValuesByProperty);
        }
    }

    public static class InvalidFileNameException extends ImageException {
        public InvalidFileNameException() {
            super(ErrorCode.INVALID_FILE_NAME);
        }
    }
}

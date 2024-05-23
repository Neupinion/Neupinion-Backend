package com.neupinion.neupinion.opinion.exception;

import com.neupinion.neupinion.globalexception.CustomException;
import com.neupinion.neupinion.globalexception.ErrorCode;

public class OpinionLikeException extends CustomException {

    protected OpinionLikeException(final ErrorCode errorCode) {
        super(errorCode);
    }
}

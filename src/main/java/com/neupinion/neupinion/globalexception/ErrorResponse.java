package com.neupinion.neupinion.globalexception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ErrorResponse {

    private final int code;
    private final String message;

    public static ErrorResponse from(final CustomException customException) {
        return new ErrorResponse(customException.getCode(), customException.getMessage());
    }
}

package com.neupinion.neupinion.globalexception;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Getter
public class CustomException extends RuntimeException{

    private static final String EXCEPTION_INFO_BRACKET = "{ %s | %s }";
    private static final String CODE_MESSAGE = " Code: %d, Message: %s ";
    private static final String PROPERTY_VALUE = "Property: %s, Value: %s ";
    private static final String VALUE_DELIMITER = "/";

    private final int code;
    private final String message;
    private final Map<String, String> inputValuesByProperty;

    protected CustomException(final ErrorCode errorCode) {
        this(errorCode, Collections.emptyMap());
    }

    protected CustomException(
        final ErrorCode errorCode,
        final Map<String, String> inputValuesByProperty
    ) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.inputValuesByProperty = inputValuesByProperty;
    }

    public static CustomException of(
        final ErrorCode errorCode,
        final Map<String, String> propertyValues
    ) {
        return new CustomException(errorCode, propertyValues);
    }

    public static CustomException from(final ErrorCode errorCode) {
        return new CustomException(errorCode);
    }

    public String getErrorInfoLog() {
        final String codeMessage = String.format(CODE_MESSAGE, code, message);
        final String errorPropertyValue = getErrorPropertyValue();

        return String.format(EXCEPTION_INFO_BRACKET, codeMessage, errorPropertyValue);
    }

    private String getErrorPropertyValue() {
        return inputValuesByProperty.entrySet()
            .stream()
            .map(entry -> String.format(PROPERTY_VALUE, entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(VALUE_DELIMITER));
    }
}

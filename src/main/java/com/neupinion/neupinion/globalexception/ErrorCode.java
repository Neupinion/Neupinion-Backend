package com.neupinion.neupinion.globalexception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ErrorCode {

    // 1000: 이슈
    EMPTY_REPROCESSED_ISSUE(1000, "이슈에 재가공 이슈가 있어야 합니다."),
    ;

    private final int code;
    private final String message;
}

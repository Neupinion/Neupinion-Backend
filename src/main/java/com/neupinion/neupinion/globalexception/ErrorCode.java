package com.neupinion.neupinion.globalexception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ErrorCode {

    // 1000: 이슈
    EMPTY_REPROCESSED_ISSUE(1000, "이슈에 재가공 이슈가 있어야 합니다."),
    NULL_OR_EMPTY_ISSUE_TITLE(1001, "이슈의 제목은 공백이나 빈칸이 될 수 없습니다."),
    TOO_LONG_ISSUE_TITLE(1002, "이슈의 제목은 100자 이하여야 합니다."),
    ;

    private final int code;
    private final String message;
}

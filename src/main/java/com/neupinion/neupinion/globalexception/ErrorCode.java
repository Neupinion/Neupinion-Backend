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
    CATEGORY_NOT_FOUND_EXCEPTION(1003, "해당 카테고리를 찾을 수 없습니다."),
    REPROCESSED_ISSUE_NOT_FOUND(1004, "해당 재가공 이슈를 찾을 수 없습니다."),
    FOLLOW_UP_ISSUE_TAG_NOT_FOUND(1005, "해당하는 후속 이슈 태그를 찾을 수 없습니다."),
    TOO_LONG_ISSUE_COMMENT_EXCEPTION(1006, "이슈 코멘트는 1000자 이하여야 합니다."),
    INVALID_VIEW_MODE(1007, "잘못된 조회 인자입니다."),
    FOLLOW_UP_ISSUE_NOT_FOUND(1008, "해당 후속 이슈를 찾을 수 없습니다."),
    PARAGRAPH_NOT_FOUND(1009, "해당 문단을 찾을 수 없습니다."),
    PARAGRAPH_FOR_OTHER_ISSUE(1010, "해당 문단은 다른 이슈에 속해 있습니다."),

    // 2000: 의견
    TOO_LONG_OPINION_CONTENT(2001, "의견 내용은 300자 이하여야 합니다."),
    EMPTY_OPINION_CONTENT(2002, "의견 내용은 공백이나 빈칸이 될 수 없습니다."),
    ALREADY_EXIST_OPINION(2003, "이미 동일한 문단에 의견을 등록하였습니다."),

    // 3000: 회원
    MEMBER_NOT_FOUND(3001, "해당 회원을 찾을 수 없습니다."),
    ;

    private final int code;
    private final String message;
}

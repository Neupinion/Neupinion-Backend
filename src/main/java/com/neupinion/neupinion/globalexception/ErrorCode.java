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
    ISSUE_TYPE_NOT_FOUND(1011, "해당 이슈 타입을 찾을 수 없습니다."),

    // 2000: 의견
    TOO_LONG_OPINION_CONTENT(2000, "의견 내용은 300자 이하여야 합니다."),
    EMPTY_OPINION_CONTENT(2001, "의견 내용은 공백이나 빈칸이 될 수 없습니다."),
    ALREADY_EXIST_OPINION(2002, "이미 동일한 문단에 의견을 등록하였습니다."),
    NOT_FOUND_OPINION(2003, "해당 의견을 찾을 수 없습니다."),
    NOT_MATCHED_MEMBER(2004, "자신의 의견만 수정할 수 있습니다."),
    INVALID_OPINION_VIEW_MODE(2005, "잘못된 조회 인자입니다."),

    // 3000: 회원
    MEMBER_NOT_FOUND(3000, "해당 회원을 찾을 수 없습니다."),
    NICKNAME_LENGTH_EXCEEDED(3001, "닉네임은 30자 이하여야 합니다."),

    // 4000: 신뢰도 평가
    VOTE_STATUS_NOT_FOUND(4000, "해당 평가 상태를 찾을 수 없습니다."),

    // 5000: 인증, 인가
    OAUTH_TYPE_NOT_FOUND(5000, "해당 OAuth 타입을 찾을 수 없습니다."),
    INVALID_AUTHORIZATION_CODE(5001, "잘못된 인증 코드입니다."),
    KAKAO_SERVER_EXCEPTION(5002, "카카오 서버에서 오류가 발생하였습니다."),

    // 6000: 토큰
    EXPIRED_TOKEN(6000, "토큰이 만료되었습니다."),
    NOT_ISSUED_TOKEN(6001, "토큰이 발급되지 않았습니다."),
    NOT_FOUND_REFRESH_TOKEN(6002, "리프레시 토큰을 찾을 수 없습니다."),
    TOKEN_PAIR_NOT_MATCHING_EXCEPTION(6003, "토큰 쌍이 일치하지 않습니다."),
    NOT_FOUND_ACCESS_TOKEN(6004, "액세스 토큰을 찾을 수 없습니다."),
    ;

    private final int code;
    private final String message;
}

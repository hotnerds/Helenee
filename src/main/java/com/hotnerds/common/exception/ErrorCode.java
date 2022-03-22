package com.hotnerds.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
//enum 자체를 object 형태로 반환.
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    //Common
    INVALID_INPUT_VALUE(400, "C001", "유효하지 않은 입력입니다."),
    INTERNAL_SERVER_ERROR(500, "C002", "서버에 문제가 있습니다."),
    METHOD_NOT_ALLOWED(405, "C003", "허용된 메서드가 아닙니다."),
    INVALID_TYPE_VALUE(400, "C004", "타입이 유효하지 않습니다"),

    //Post
    POST_NOT_FOUND_EXCEPTION(404, "P001", "해당하는 게시물을 찾을 수 없습니다."),

    //Tag
    TAG_NAME_NOT_VALID_EXCEPTION(400, "T001", "유효하지 않은 태그 이름입니다."),

    //Like
    LIKE_NOT_FOUND_EXCEPTION(404, "P002", "게시물에 좋아요를 누르지 않았습니다."),
    DUPLICATED_LIKE_EXCEPTION(400, "P003", "좋아요 요청이 중복되었습니다."),

    // User
    USER_NOT_FOUND_EXCEPTION(404, "U001", "해당하는 사용자를 찾을 수 없습니다."),
    USER_DUPLICATED_EXCEPTION(400, "U002", "동일한 정보를 가진 유저가 이미 존재합니다"),

    //Follow
    FOLLOW_DUPLICATED_EXCEPTION(400, "F001","동일한 팔로우 관계가 이미 존재합니다."),
    FOLLOW_NOT_FOUND_EXCEPTION(404, "F002", "팔로우가 존재하지 않습니다."),

    //Diet
    DIET_DUPLICATED_EXCEPTION(400, "D001", "동일한 정보를 가진 식단이 이미 존재합니다."),
    DIET_NOT_FOUND_EXCEPTION(404, "D002", "해당하는 식단이 없습니다."),

    //Auth
    AUTHENTICATION_EXCEPTION(400, "AU001", "인증 관련 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;
}

package com.hotnerds.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
//enum 자체를 object 형태로 반환.
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", "유효하지 않은 입력입니다."),
    INTERNAL_SERVER_ERROR(500, "C002", "서버에 문제가 있습니다."),
    METHOD_NOT_ALLOWED(405, "C003", "허용된 메서드가 아닙니다."),
    INVALID_TYPE_VALUE(400, "C004", "타입이 유효하지 않습니다"),

    // Post
    POST_NOT_FOUND_EXCEPTION(404, "P001", "해당하는 게시물을 찾을 수 없습니다."),
    POST_WRITER_NOT_MATCH_EXCEPTION(400, "P002", "해당 게시물 작성자가 아닙니다."),

    // Tag
    TAG_NAME_NOT_VALID_EXCEPTION(400, "T001", "유효하지 않은 태그 이름입니다."),
    DUPLICATED_TAG_EXCEPTION(400, "T002", "태그가 중복되었습니다."),
    TAG_NOT_FOUND_EXCEPTION(404, "T003", "태그를 찾을 수 없습니다."),

    // Like
    LIKE_NOT_FOUND_EXCEPTION(404, "P002", "게시물에 좋아요를 누르지 않았습니다."),
    DUPLICATED_LIKE_EXCEPTION(400, "P003", "좋아요 요청이 중복되었습니다."),

    // Comment
    COMMENT_NOT_FOUND_EXCEPTION(404, "CM002", "해당 정보를 가진 댓글이 존재하지 않습니다."),
    COMMENT_DUPLICATED_EXCEPTION(400, "CM003", "댓글 요청이 중복되었습니다."),
    COMMENT_INVALID_EXCEPTION(405, "CM003", "적절하지 않은 댓글 생성 요청입니다."),

    // User
    USER_NOT_FOUND_EXCEPTION(404, "U001", "해당하는 사용자를 찾을 수 없습니다."),
    USER_DUPLICATED_EXCEPTION(400, "U002", "동일한 정보를 가진 유저가 이미 존재합니다"),
    USER_INVALID_EXCEPTION(403, "C005", "유효하지 않은 사용자의 요청입니다."),

    // Follow
    FOLLOW_DUPLICATED_EXCEPTION(400, "F001","동일한 팔로우 관계가 이미 존재합니다."),
    FOLLOW_NOT_FOUND_EXCEPTION(404, "F002", "팔로우가 존재하지 않습니다."),

    // Diet
    DIET_DUPLICATED_EXCEPTION(400, "D001", "동일한 정보를 가진 식단이 이미 존재합니다."),
    DIET_NOT_FOUND_EXCEPTION(404, "D002", "해당하는 식단이 없습니다."),
    INVALID_MEALTIME_VALUE(400, "D003", "mealtime 값이 유효하지 않습니다."),

    //Food
    FOOD_NOT_FOUND_EXCEPTION(404, "F001", "음식이 존재하지 않습니다."),

    // Auth
    AUTHENTICATION_EXCEPTION(400, "AU001", "인증 관련 오류가 발생했습니다."),

    //Goal
    GOAL_NOT_FOUND_EXCEPTION(404, "G001", "특정 날짜에 Goal을 찾지 못했습니다.");

    private final int status;
    private final String code;
    private final String message;
}

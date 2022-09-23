package com.project.raidho.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /*
    400
     */
    DIDNT_CHECK_LIKE(HttpStatus.BAD_REQUEST, "해당 게시글에 좋아요를 체크한 적이 없습니다."),
    ALREADY_JOIN_CHAT_ROOM(HttpStatus.BAD_REQUEST, "이미 참여한 대화방입니다."),
    THIS_ROOM_IS_FULL(HttpStatus.BAD_REQUEST, "해당 채팅방에 참여할 수 있는 자리가 없습니다."),
    /*
    401
     */

    /*
    403
     */
    UNAUTHORIZATION_MEMBER(HttpStatus.UNAUTHORIZED, "회원만 사용가능합니다."),
    INVALID_AUTH_MEMBER_DELETE(HttpStatus.UNAUTHORIZED, "작성자 본인만 삭제가 가능합니다."),
    INVALID_AUTH_MEMBER_UPDATE(HttpStatus.UNAUTHORIZED, "작성자 본인만 수정이 가능합니다."),


    /*
    404
     */
    DOESNT_EXIST_MEMBER(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."),
    DOESNT_EXIST_POST(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다.");


    private final HttpStatus httpStatus;
    private final String errorMessage;

     ErrorCode(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }
}

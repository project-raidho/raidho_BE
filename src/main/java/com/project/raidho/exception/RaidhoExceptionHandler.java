package com.project.raidho.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice /* 프로젝트 전역에서 발생하는 모든 예외를 잡아줍니다. */
public class RaidhoExceptionHandler extends ResponseEntityExceptionHandler { // ResponseEntityExceptionHandler 를 추가로 상속해주면서 글로벌 전역처리

    @ExceptionHandler(value = {RaidhoException.class})
    protected ResponseEntity<ErrorResponse> handleDataException(RaidhoException e) {
        log.error("RaidhoException throw CustomException : {}", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

}

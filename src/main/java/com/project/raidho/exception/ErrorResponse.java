package com.project.raidho.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Builder
@Getter
public class ErrorResponse {

    private String error;

    private final String errorMessage;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .error(errorCode.getHttpStatus().name())
                        .errorMessage(errorCode.getErrorMessage())
                        .build());
    }

}

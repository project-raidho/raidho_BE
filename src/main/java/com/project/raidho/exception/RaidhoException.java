package com.project.raidho.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RaidhoException extends Exception{
    private final ErrorCode errorCode;
}

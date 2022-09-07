package com.project.raidho.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    USER("ROLE_USER","회원"),  // 사용자 권한
    ADMIN("ROLE_ADMIN","관리자");  // 관리자 권한

    private final String key;
    private final String title;
}

package com.project.raidho.domain.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JwtTokenDto {
    private String authorization; // accessToken
    private String refreshToken; // refreshToken
}

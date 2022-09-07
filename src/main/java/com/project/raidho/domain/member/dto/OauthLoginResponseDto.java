package com.project.raidho.domain.member.dto;

import com.project.raidho.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OauthLoginResponseDto {

    private HttpHeaders httpHeaders;
    private Member member;
}

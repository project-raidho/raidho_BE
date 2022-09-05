package com.project.raidho.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MemberDto {

    private Long id;
    private String memberName;
    private String memberImage;
    private String memberIntro;

    public MemberDto (OauthLoginResponseDto oauthLoginResponseDto) {
        this.id = oauthLoginResponseDto.getMember().getId();
        this.memberName = oauthLoginResponseDto.getMember().getMemberName();
        this.memberImage = oauthLoginResponseDto.getMember().getMemberImage();
        this.memberIntro = oauthLoginResponseDto.getMember().getMemberIntro();
    }
}
package com.project.raidho.domain;

import com.project.raidho.domain.member.dto.OauthLoginResponseDto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class MembersResponseDto {

    private Long id;
    private String memberName;
    private String memberImage;
    private String memberIntro;
    @Builder
    public MembersResponseDto(OauthLoginResponseDto oauthLoginResponseDto) {
        this.id = oauthLoginResponseDto.getMember().getId();
        this.memberName = oauthLoginResponseDto.getMember().getMemberName();
        this.memberImage = oauthLoginResponseDto.getMember().getMemberImage();
        this.memberIntro = oauthLoginResponseDto.getMember().getMemberIntro();
    }



}

package com.project.raidho.domain.member.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MembersResponseDto {
    private String memberName;
    private String memberImage;
//    @Builder
//    public MembersResponseDto(String memberName, String memberImage) {
//        this.memberName = memberName;
//        this.memberImage = memberImage;
//    }
}

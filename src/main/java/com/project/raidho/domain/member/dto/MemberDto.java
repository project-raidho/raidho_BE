package com.project.raidho.domain.member.dto;

import com.project.raidho.domain.member.Member;
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

    @Builder
    public MemberDto (Member member) {
        this.id = member.getId();
        this.memberName = member.getMemberName();
        this.memberImage = member.getMemberImage();
        this.memberIntro = member.getMemberIntro();
    }

}

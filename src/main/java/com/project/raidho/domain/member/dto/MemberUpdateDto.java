package com.project.raidho.domain.member.dto;

import com.project.raidho.domain.s3.MultipartFiles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberUpdateDto {
    private String memberName;
    private MultipartFiles memberImage;
    private String memberIntro;
}

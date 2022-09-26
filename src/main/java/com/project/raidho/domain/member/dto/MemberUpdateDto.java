package com.project.raidho.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberUpdateDto {

    private String memberName;
    private MultipartFile memberImage;
    private String memberIntro;

}

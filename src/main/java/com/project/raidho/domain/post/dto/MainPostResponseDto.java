package com.project.raidho.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MainPostResponseDto {

    private Long id;
    private Boolean isMine;
    private Boolean isHeartMine;
    private Boolean isImages;
    private String memberName;
    private String memberImage;
    private List<String> multipartFiles;
    private int heartCount;
    private LocalDate createdAt;
    private LocalDate modifiedAt;

}

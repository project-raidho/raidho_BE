package com.project.raidho.domain.post.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private Boolean isMine;
    private Boolean isHeartMine;
    private Boolean isImages;
    private String memberName;
    private String memberImage;
    private String content;
    private List<String> multipartFiles;
    private List<String> tags;
    private List<String> locationTags;
    private int heartCount;
    private LocalDate createdAt;
    private LocalDate modifiedAt;

}

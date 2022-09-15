package com.project.raidho.domain.post.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private Boolean isMine;
    private Boolean isHeartMine;
    private String memberName;
    private String memberImage;
    private String content;
    private List<String> multipartFiles;
    private List<String> tags;
    private List<String> locationTags;
    private int heartCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}

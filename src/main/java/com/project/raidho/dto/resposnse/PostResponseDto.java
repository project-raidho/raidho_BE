package com.project.raidho.dto.resposnse;

import com.project.raidho.domain.MembersResponseDto;
import com.project.raidho.domain.MultipartFiles;
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
    private String content;
    private MembersResponseDto membersResponseDto;
//    private List<MultipartFile> multipartFiles;
    private List<String> multipartFiles;
    private List<String> tags;
    private List<String> locationTags;
    private int heartCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}

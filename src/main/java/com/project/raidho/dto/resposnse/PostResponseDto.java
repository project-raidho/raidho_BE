package com.project.raidho.dto.resposnse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String content;
    private List<MultipartFile> multipartFiles;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}

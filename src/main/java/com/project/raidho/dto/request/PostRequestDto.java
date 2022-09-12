package com.project.raidho.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostRequestDto {
    private String content;
    private List<MultipartFile> files;
    private List<String> tags;
}

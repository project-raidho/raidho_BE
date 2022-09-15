package com.project.raidho.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor

public class UpdatePostRequestDto {
    private String content;
    private List<String> tags;
    private List<String> locationTags;
}

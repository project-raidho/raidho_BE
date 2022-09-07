package com.project.raidho.dto.request;


import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class PostRequestDto {
    @NotNull
    private final String title;
    @NotNull
    private final String contents;
    private final MultipartFile images;
}

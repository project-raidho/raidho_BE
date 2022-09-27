package com.project.raidho.domain.comment.dto;

import com.project.raidho.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String memberName;
    private String memberImage;
    private String content;
    private LocalDate createdAt;
    private LocalDate modifiedAt;
    private Boolean isMine;
}

package com.project.raidho.domain.meetingPost.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingResponseDto {
    private Long id;
    private String meetingTags;
    private String theme;

}

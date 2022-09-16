package com.project.raidho.domain.meetingPost.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingPostResponseDto {
    private Long id;
    private String themeCategory;
    private List<String> meetingTags;
    private String title;
    private String desc;
    private String startDate;
    private String endDate;
    private int people;
    private String roomCloseDate;
    private String departLocation;

}

package com.project.raidho.domain.meetingPost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MeetingPostRequestDto {

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

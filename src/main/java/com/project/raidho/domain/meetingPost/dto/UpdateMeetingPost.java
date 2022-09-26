package com.project.raidho.domain.meetingPost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMeetingPost {

    private String title;
    private String desc;
    private String startDate;
    private String endDate;
    private int people;
    private String roomCloseDate;
    private String departLocation;

}

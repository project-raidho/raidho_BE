package com.project.raidho.domain.meetingPost.dto;

import lombok.*;

import java.time.LocalDate;
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
    private int memberCount;
    private String roomCloseDate;
    private String departLocation;
    private Boolean isMine;
    private Boolean isAlreadyJoin;
    private int meetingStatus;
    private int starCount;
    private String memberName;
    private String memberImage;
    private LocalDate createdAt;
    private LocalDate modifiedAt;

}

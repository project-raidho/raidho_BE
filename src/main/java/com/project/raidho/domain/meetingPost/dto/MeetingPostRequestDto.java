package com.project.raidho.domain.meetingPost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MeetingPostRequestDto {

    private List<String> meetingTags;
}

package com.project.raidho.domain.chat.ChatDto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EachRoomInfoDto {

    private String themeCategory;
    private List<String> meetingTags;
    private List<String> memberNames;
    private String title;
    private String desc;
    private String startDate;
    private String endDate;
    private int people; // 채팅방 총 입장 인원 수 제한
    private int memberCount; // 채팅방에 들어온 인원 수
    private String roomCloseDate;
    private String departLocation;
    private Boolean isMine;

}

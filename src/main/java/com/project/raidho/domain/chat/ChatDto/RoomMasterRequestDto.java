package com.project.raidho.domain.chat.ChatDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomMasterRequestDto  {

    private String roomName;
    private Long meetingPostId;
    private int people;

}

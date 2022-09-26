package com.project.raidho.domain.chat.ChatDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMasterResponseDto {

    private Long roomMasterId;
    private String roomName;
    private String roomPic;
    private String recentChat;
    private Long unReadCount;
    private int people;

}

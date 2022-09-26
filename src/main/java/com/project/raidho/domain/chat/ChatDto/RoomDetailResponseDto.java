package com.project.raidho.domain.chat.ChatDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoomDetailResponseDto {

    private Long roomMasterId;
    private String roomName;
    private int people;

}

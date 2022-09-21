package com.project.raidho.domain.chat.ChatDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatDto {

    public enum Type {
        ENTER, TALK, QUIT
    }
    private Type type;
    private String roomId;
    private String sender;
    private String message;
    private Long memberId;
    private String memberImage;

}

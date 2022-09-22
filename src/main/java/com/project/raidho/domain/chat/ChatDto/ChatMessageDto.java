package com.project.raidho.domain.chat.ChatDto;

import com.project.raidho.domain.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMessageDto {

    private ChatMessage.Type type;
    private String roomId;
    private String sender;
    private String message;
    private Long memberId;
    private String memberImage;
    private String messageTime;

}

package com.project.raidho.domain.chat.ChatDto;

import com.project.raidho.domain.chat.ChatMessage;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatMessageDto {

    private ChatMessage.Type type;
    private String roomId;
    private String sender;
    private String message;
    private Long memberId;
    private String memberImage;
    private String messageTime;

}

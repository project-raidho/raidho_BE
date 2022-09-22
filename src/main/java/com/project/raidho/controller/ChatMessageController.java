package com.project.raidho.controller;

import com.project.raidho.domain.chat.ChatDto.ChatMessageDto;
import com.project.raidho.domain.chat.ChatMessage;
import com.project.raidho.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final SimpMessageSendingOperations messageSendingOperations;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/send/{roomId}")
    public void getRoomChats(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto) {
        ChatMessageDto returnChatMessageDto = chatMessageService.saveChatMessage(roomId, chatMessageDto); // db 메시지 저장
        messageSendingOperations.convertAndSend("/sub/chat/message/" + roomId, returnChatMessageDto);
    }
}

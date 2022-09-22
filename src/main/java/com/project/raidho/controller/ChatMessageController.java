package com.project.raidho.controller;

import com.project.raidho.domain.chat.ChatDto.ChatMessageDto;
import com.project.raidho.domain.chat.ChatMessage;
import com.project.raidho.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final SimpMessageSendingOperations messageSendingOperations;


    @MessageMapping("/chat/send/{roomId}")
    public void getRoomChats(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto) {
        messageSendingOperations.convertAndSend("/sub/chat/message/" + roomId, chatMessageDto);


    }
}

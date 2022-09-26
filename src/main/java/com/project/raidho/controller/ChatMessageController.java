package com.project.raidho.controller;

import com.project.raidho.domain.chat.ChatDto.ChatMessageDto;
import com.project.raidho.redis.RedisPublisher;
import com.project.raidho.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private Map<String, ChannelTopic> topics;
    private final SimpMessageSendingOperations messageSendingOperations;
    private final ChatMessageService chatMessageService;
    private final RedisPublisher redisPublisher;

    @MessageMapping("/chat/send/{roomId}")
    public void getRoomChats(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto) {
        ChatMessageDto returnChatMessageDto = chatMessageService.saveChatMessage(roomId, chatMessageDto); // db 메시지 저장
        redisPublisher.publish(getTopic(String.valueOf(roomId)),chatMessageDto);
        System.out.println("CHATMESSAGECONTROLLERCHATMESSAGECONTROLLERCHATMESSAGECONTROLLERCHATMESSAGECONTROLLERCHATMESSAGECONTROLLER");
        //messageSendingOperations.convertAndSend("/sub/chat/message/" + roomId, returnChatMessageDto);
    }

    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }
}

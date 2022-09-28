package com.project.raidho.controller;

import com.project.raidho.domain.chat.ChatDto.ChatMessageDto;
import com.project.raidho.domain.chat.ChatMessage;
import com.project.raidho.redis.RedisPublisher;
import com.project.raidho.service.ChatMessageService;
import com.project.raidho.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
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
    private final RoomService roomService;

    @MessageMapping("/chat/send/{roomId}")
    public void getRoomChats(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto, @Header("token") String token) {

        System.out.println("================================================");
        System.out.println(token);
        System.out.println("================================================");

        if (ChatMessage.Type.ENTER.equals(chatMessageDto.getType())) {
            chatMessageDto.setMessage(chatMessageDto.getSender() + "님이 입장하셨습니다.");
            roomService.enterChatRoom(String.valueOf(roomId));
            ChatMessageDto returnChatMessageDto = chatMessageService.saveChatMessage(roomId, chatMessageDto); // db 메시지 저장
            redisPublisher.publish(roomService.getTopic(String.valueOf(roomId)),chatMessageDto);
        } else {
            roomService.enterChatRoom(String.valueOf(roomId));
            ChatMessageDto returnChatMessageDto = chatMessageService.saveChatMessage(roomId, chatMessageDto); // db 메시지 저장
            redisPublisher.publish(roomService.getTopic(String.valueOf(roomId)), chatMessageDto);
        }
        //messageSendingOperations.convertAndSend("/sub/chat/message/" + roomId, returnChatMessageDto);
    }
}

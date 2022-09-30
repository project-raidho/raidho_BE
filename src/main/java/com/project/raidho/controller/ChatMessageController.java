package com.project.raidho.controller;

import com.project.raidho.domain.chat.ChatDto.ChatMessageDto;
import com.project.raidho.redis.RedisPublisher;
import com.project.raidho.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final RedisPublisher redisPublisher;
    private final RoomService roomService;
    // 채팅 보내기
    @MessageMapping("/chat/send/{roomId}")
    public void getRoomChats(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto, @Header("token") String token) {

//        if (ChatMessage.Type.ENTER.equals(chatMessageDto.getType())) {
//            chatMessageDto.setMessage(chatMessageDto.getSender() + " 님이 입장하셨습니다.");
//            ChatMessage chatMessage = ChatMessage.builder().message(chatMessageDto.getMessage())
//                            .type(chatMessageDto.getType()).sender(chatMessageDto.getSender()).roomId(roomId).build();
//            chatMessageRepository.save(chatMessage);
//            roomService.enterChatRoom(String.valueOf(roomId));
//            redisPublisher.publish(roomService.getTopic(String.valueOf(roomId)),chatMessageDto);
//        } else {
//            roomService.enterChatRoom(String.valueOf(roomId));
//            ChatMessageDto returnChatMessageDto = chatMessageService.saveChatMessage(roomId, chatMessageDto); // db 메시지 저장
//            redisPublisher.publish(roomService.getTopic(String.valueOf(roomId)), chatMessageDto);
//        }
        roomService.enterChatRoom(String.valueOf(roomId));
//        ChatMessageDto returnChatMessageDto = chatMessageService.saveChatMessage(roomId, chatMessageDto); // db 메시지 저장
        redisPublisher.publish(roomService.getTopic(String.valueOf(roomId)), chatMessageDto);
        //messageSendingOperations.convertAndSend("/sub/chat/message/" + roomId, returnChatMessageDto);
    }
}

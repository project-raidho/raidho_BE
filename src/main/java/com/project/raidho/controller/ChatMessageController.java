package com.project.raidho.controller;

import com.project.raidho.domain.chat.ChatDto.ChatMessageDto;
import com.project.raidho.domain.chat.ChatMessage;
import com.project.raidho.domain.member.Member;
import com.project.raidho.jwt.JwtTokenProvider;
import com.project.raidho.redis.RedisPublisher;
import com.project.raidho.repository.ChatMessageRepository;
import com.project.raidho.service.ChatMessageService;
import com.project.raidho.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private Map<String, ChannelTopic> topics;
    private final SimpMessageSendingOperations messageSendingOperations;
    private final ChatMessageService chatMessageService;
    private final RedisPublisher redisPublisher;
    private final RoomService roomService;
    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat/send/{roomId}")
    public void getRoomChats(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto, @Header("token") String token) {

        if (ChatMessage.Type.ENTER.equals(chatMessageDto.getType())) {
            chatMessageDto.setMessage(chatMessageDto.getSender() + " 님이 입장하셨습니다.");
            ChatMessage chatMessage = ChatMessage.builder().message(chatMessageDto.getMessage())
                            .type(chatMessageDto.getType()).sender(chatMessageDto.getSender()).roomId(roomId).build();
            chatMessageRepository.save(chatMessage);
            roomService.enterChatRoom(String.valueOf(roomId));
            redisPublisher.publish(roomService.getTopic(String.valueOf(roomId)),chatMessageDto);
        } else {
            roomService.enterChatRoom(String.valueOf(roomId));
            ChatMessageDto returnChatMessageDto = chatMessageService.saveChatMessage(roomId, chatMessageDto); // db 메시지 저장
            redisPublisher.publish(roomService.getTopic(String.valueOf(roomId)), chatMessageDto);
        }
        //messageSendingOperations.convertAndSend("/sub/chat/message/" + roomId, returnChatMessageDto);
    }
}

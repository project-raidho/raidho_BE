package com.project.raidho.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.raidho.domain.chat.ChatDto.ChatMessageDto;
import com.project.raidho.logging.Logging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messageSendingOperations;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            System.out.println(message);
            System.out.println(message.getBody());
            System.out.println("ONONONONONONONONONONONONONONONONONONONONONONONONONONONONONONONONONONONON");
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            ChatMessageDto chatMessageDto = objectMapper.readValue(publishMessage, ChatMessageDto.class);
            messageSendingOperations.convertAndSend("/sub/chat/message/" + chatMessageDto.getRoomId(), chatMessageDto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

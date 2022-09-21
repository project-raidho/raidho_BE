package com.project.raidho.controller;

import com.project.raidho.domain.chat.ChatDto.ChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final SimpMessageSendingOperations messageSendingOperations;

    @MessageMapping("/chat/send/{roomId}")
    public void getRoomChats(@DestinationVariable Long roomId, ChatDto chatDto) {
        System.out.println("12398723842398473290847309847230984");
        if (ChatDto.Type.ENTER.equals(chatDto.getType())) {
            chatDto.setMessage(chatDto.getSender() + " 님이 입장하셨습니다.");
            System.out.println("kjvnkjnkjdsnksdjnfdskjfndskjfndskjfnsdkljf");
            messageSendingOperations.convertAndSend("/sub/chat/message/" + roomId, chatDto);
            System.out.println("sdkj whkfwkojf dihoiu dquid hqoiudaskljd asud hoakshd askjdh alksjdhaskljd ");
        }
    }
}

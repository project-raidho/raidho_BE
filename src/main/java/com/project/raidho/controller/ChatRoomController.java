package com.project.raidho.controller;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.chat.ChatDto.RoomMasterRequestDto;
import com.project.raidho.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/chat")
@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    public final ChatService chatService;

    @PostMapping ("/roomcreate")
    public ResponseEntity<?> createRoom(@RequestBody RoomMasterRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        chatService.createRoom(requestDto,userDetails);
                return null;
    }
}

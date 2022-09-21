package com.project.raidho.controller;

import com.project.raidho.domain.chat.ChatDto.RoomMasterRequestDto;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.service.RoomService;
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

    public final RoomService roomService;

    // 채팅방 만들기
    @PostMapping ("/room/create")
    public ResponseEntity<?> createRoom(@RequestBody RoomMasterRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) throws RaidhoException {
        return ResponseEntity.ok().body(roomService.createRoom(userDetails,requestDto));
    }

    // 채팅방 입장
    @GetMapping("/chatting/{roomId}")
    public ResponseEntity<?> roomDetail(@PathVariable String roomId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body("??????????????");
    }
}

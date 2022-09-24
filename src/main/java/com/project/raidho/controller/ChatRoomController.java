package com.project.raidho.controller;

import com.project.raidho.domain.chat.ChatDto.ChatMessageDto;
import com.project.raidho.domain.chat.ChatDto.RoomMasterRequestDto;
import com.project.raidho.domain.meetingPost.dto.MeetingPostRequestDto;
import com.project.raidho.domain.meetingPost.dto.UpdateMeetingPost;
import com.project.raidho.domain.post.dto.UpdatePostRequestDto;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.service.ChatMessageService;
import com.project.raidho.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/chat")
@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final RoomService roomService;
    private final ChatMessageService chatMessageService;

    // 채팅방 만들기
    @PostMapping ("/room/create")
    public ResponseEntity<?> createRoom(@RequestBody RoomMasterRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) throws RaidhoException {
        return ResponseEntity.ok().body(roomService.createRoom(userDetails,requestDto));
    }

    // 채팅방 입장
    @GetMapping("/chatting/{roomId}")
    public ResponseEntity<?> roomDetail(@PathVariable Long roomId, @AuthenticationPrincipal UserDetails userDetails) throws RaidhoException {
        return ResponseEntity.ok().body(roomService.joinChatRoom(roomId, userDetails));
    }

    // 내 채팅방 리스트 가져오기
    @GetMapping("/chatList")
    public ResponseEntity<?> myChatRooms(@AuthenticationPrincipal UserDetails userDetails) throws RaidhoException {
        return ResponseEntity.ok().body(roomService.myChatRooms(userDetails));
    }

    @GetMapping("/messages/{roomId}")
    public Page<ChatMessageDto> getAllChatMessageList(@PathVariable Long roomId, @PageableDefault Pageable pageable) {
        return chatMessageService.getAllChatMessageList(roomId, pageable);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<?> eachChatRoomInfo(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long roomId) {
        return ResponseEntity.ok().body(roomService.eachChatRoomInfo(userDetails,roomId));
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<?> exitChatRoom(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long roomId) {
        roomService.exitChatRoom(userDetails, roomId);
        return ResponseEntity.ok().body("채팅방에서 탈퇴하셨습니다.");
    }
    @PutMapping("/{roomId}")
    public ResponseEntity<?> updateMeetingPost(@PathVariable("roomId") Long roomId,
                                               @AuthenticationPrincipal UserDetails userDetails,
                                               UpdateMeetingPost updateMeetingPost) {
        return roomService.updateMeetingPost(roomId,userDetails,updateMeetingPost);
    }

}

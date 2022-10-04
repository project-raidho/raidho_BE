package com.project.raidho.service;

import com.project.raidho.domain.chat.ChatDto.ChatMessageDto;
import com.project.raidho.domain.chat.ChatMessage;
import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.member.Member;
import com.project.raidho.repository.ChatMessageRepository;
import com.project.raidho.repository.RoomDetailRepository;
import com.project.raidho.repository.RoomMasterRepository;
import com.project.raidho.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    private final RoomDetailRepository roomDetailRepository;
    private final RoomMasterRepository roomMasterRepository;

    @Transactional
    public ChatMessageDto saveChatMessage(Long roomId, ChatMessageDto chatMessageDto) {
        RoomDetail roomDetail = roomDetailRepository.findByRoomMaster_RoomIdAndMember_Id(roomId, chatMessageDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 속해 있지 않은 회원입니다."));
        Member member = roomDetail.getMember();
        SimpleDateFormat format = new SimpleDateFormat("a HH:mm");
        Date time = new Date();
        String stime = format.format(time);
        chatMessageDto.setMessageTime(stime);
        ChatMessage chatMessage = ChatMessage.builder()
                .roomId(roomId)
                .message(chatMessageDto.getMessage())
                .member(member)
                .memberImage(chatMessageDto.getMemberImage())
                .sender(chatMessageDto.getSender())
                .type(chatMessageDto.getType())
                .messageTime(stime)
                .build();
        chatMessageRepository.save(chatMessage);
        return chatMessageDto;
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageDto> getAllChatMessageList(Long roomId, Pageable pageable, UserDetails userDetails) {

        Long memberId = ((PrincipalDetails) userDetails).getMember().getId();
        RoomDetail roomDetail = roomDetailRepository.findByRoomMaster_RoomIdAndMember_Id(roomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 속해있지 않은 회원입니다."));
        LocalDateTime ldt = roomDetail.getCreatedAt();

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() -1);
        pageable = PageRequest.of(page, 1000);
        //return chatMessageRepository.findByRoomId(roomId, pageable);

        Page<ChatMessage> chatMessagePage = chatMessageRepository.findAllByRoomIdAndCreateAtOrderByCreatedAtAsc(roomId, ldt, pageable);

        //Page<ChatMessage> chatMessagePage = chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId, pageable);
        List<ChatMessageDto> chatMessageDtoPage = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessagePage) {
            chatMessageDtoPage.add(
                    ChatMessageDto.builder()
                            .type(chatMessage.getType())
                            .roomId(chatMessage.getRoomId().toString())
                            .sender(chatMessage.getSender())
                            .message(chatMessage.getMessage())
                            .memberId(chatMessage.getMember().getId())
                            .memberImage(chatMessage.getMemberImage())
                            .messageTime(chatMessage.getMessageTime())
                            .build()
            );
        }
        return new PageImpl<>(chatMessageDtoPage, chatMessagePage.getPageable(), chatMessagePage.getTotalElements());
    }

}

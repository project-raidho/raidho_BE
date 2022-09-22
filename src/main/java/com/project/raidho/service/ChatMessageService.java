package com.project.raidho.service;

import com.project.raidho.domain.chat.ChatDto.ChatMessageDto;
import com.project.raidho.domain.chat.ChatMessage;
import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.member.Member;
import com.project.raidho.repository.ChatMessageRepository;
import com.project.raidho.repository.RoomDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final RoomDetailRepository roomDetailRepository;

    @Transactional
    public ChatMessageDto saveChatMessage(Long roomId, ChatMessageDto chatMessageDto) {
        RoomDetail roomDetail = roomDetailRepository.findByRoomMaster_RoomIdAndMember_Id(roomId, chatMessageDto.getMemberId());
        Member member = roomDetail.getMember();
        SimpleDateFormat format = new SimpleDateFormat("a HH:mm");
        Date time = new Date();
        String stime = format.format(time);
        chatMessageDto.setCreatedAt(stime);
        ChatMessage chatMessage = ChatMessage.builder()
                .roomId(roomId)
                .message(chatMessageDto.getMessage())
                .member(member)
                .memberImage(chatMessageDto.getMemberImage())
                .sender(chatMessageDto.getSender())
                .type(chatMessageDto.getType())
                .createAt(stime)
                .build();
        chatMessageRepository.save(chatMessage);
        return chatMessageDto;
    }

    @Transactional(readOnly = true)
    public Page<ChatMessage> getAllChatMessageList(Long roomId, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() -1);
        pageable = PageRequest.of(page, 100);
        return chatMessageRepository.findByRoomId(roomId, pageable);
    }
}

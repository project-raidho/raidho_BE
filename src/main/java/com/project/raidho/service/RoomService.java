package com.project.raidho.service;

import com.project.raidho.domain.chat.ChatDto.RoomMasterRequestDto;
import com.project.raidho.domain.chat.ChatDto.RoomMasterResponseDto;
import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.member.Member;
import com.project.raidho.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RoomService {


    public RoomMasterResponseDto createRoom(Long memberId, String roomName) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        RoomMasterRequestDto requestDto = new RoomMasterRequestDto(roomName);

        return createRoom(member, requestDto);
    }


    @Transactional
    public RoomMasterResponseDto createRoom(UserDetails userDetails, RoomMasterRequestDto requestDto) {
        RoomMaster roomMaster = RoomMaster.builder()
                .roomName(requestDto.getRoomName())
                .roomDetails(new ArrayList<>())
                .roomPic(null)
                .build();

        Member member = new Member();

        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }


        RoomDetail roomDetail = RoomDetail.builder()

                .build();
    }
}

package com.project.raidho.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.project.raidho.Util.RoomUtils;
import com.project.raidho.domain.chat.ChatDto.RoomMasterRequestDto;
import com.project.raidho.domain.chat.ChatDto.RoomMasterResponseDto;
import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.member.Member;
import com.project.raidho.exception.ErrorCode;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.repository.MeetingPostRepository;
import com.project.raidho.repository.MemberRepository;
import com.project.raidho.repository.RoomMasterRepository;
import com.project.raidho.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final MemberRepository memberRepository;
    private final RoomMasterRepository roomMasterRepository;
    private final MeetingPostRepository meetingPostRepository;

    @Transactional
    public RoomMasterResponseDto createRoom(UserDetails userDetails, RoomMasterRequestDto requestDto) throws RaidhoException {
        Long memberId = ((PrincipalDetails) userDetails).getMember().getId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_MEMBER));
        MeetingPost meetingPost = meetingPostRepository.findById(requestDto.getMeetingPostId())
                .orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_POST));
        RoomMaster roomMaster = RoomMaster.builder()
                .roomId(meetingPost.getId())
                .meetingPost(meetingPost)
                .roomName(requestDto.getRoomName())
                .roomDetails(new ArrayList<>())
                .roomPic(RoomUtils.getRandomRoomPic())
                .build();
        RoomDetail roomDetail = RoomDetail.builder()
                .member(member)
                .roomMaster(roomMaster)
                .build();
        roomMaster.getRoomDetails().add(roomDetail);
        roomMasterRepository.save(roomMaster);
        return RoomMasterResponseDto.builder()
                .roomMasterId(roomMaster.getRoomId())
                .roomName(roomMaster.getRoomName())
                .roomPic(roomMaster.getRoomPic())
                .recentChat(null)
                .unReadCount(0L)
                .people(0L)
                .build();
    }
}

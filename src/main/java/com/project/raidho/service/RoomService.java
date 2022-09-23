package com.project.raidho.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.project.raidho.Util.RoomUtils;
import com.project.raidho.domain.chat.ChatDto.RoomMasterRequestDto;
import com.project.raidho.domain.chat.ChatDto.RoomMasterResponseDto;
import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.chat.ChatDto.RoomDetailResponseDto;
import com.project.raidho.domain.member.Member;
import com.project.raidho.exception.ErrorCode;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.repository.MeetingPostRepository;
import com.project.raidho.repository.MemberRepository;
import com.project.raidho.repository.RoomDetailRepository;
import com.project.raidho.repository.RoomMasterRepository;
import com.project.raidho.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final MemberRepository memberRepository;
    private final RoomMasterRepository roomMasterRepository;
    private final MeetingPostRepository meetingPostRepository;
    private final RoomDetailRepository roomDetailRepository;

    // 채팅방 생성
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
                .memberCount(requestDto.getPeople())
                .build();
        RoomDetail roomDetail = RoomDetail.builder()
                .member(member)
                .roomMaster(roomMaster)
                .build();
        roomMaster.getRoomDetails().add(roomDetail);
        roomMasterRepository.save(roomMaster);
        roomDetailRepository.save(roomDetail);
        return RoomMasterResponseDto.builder()
                .roomMasterId(roomMaster.getRoomId())
                .roomName(roomMaster.getRoomName())
                .roomPic(roomMaster.getRoomPic())
                .build();
    }

    // 채팅방 입장
    @Transactional
    public RoomDetailResponseDto joinChatRoom(Long roomId, UserDetails userDetails) throws RaidhoException {
        Long memberId = ((PrincipalDetails) userDetails).getMember().getId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_MEMBER));
        RoomMaster roomMaster = roomMasterRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 채팅방입니다."));
        int memberCount = roomDetailRepository.getCountJoinRoomMember(roomMaster);
        System.out.println("###############################");
        System.out.println("memberCount = " + memberCount );
        if (memberCount >= roomMaster.getMemberCount()) {
            throw new RaidhoException(ErrorCode.THIS_ROOM_IS_FULL);
        }
        RoomDetail roomDetail = roomDetailRepository.findByRoomMasterAndMember(roomMaster, member);
        if (roomDetail == null) {
            RoomDetail newRoomDetail = new RoomDetail(roomMaster, member);
            roomMaster.getRoomDetails().add(newRoomDetail);
            roomDetailRepository.save(newRoomDetail);
        } else {
            throw new RaidhoException(ErrorCode.ALREADY_JOIN_CHAT_ROOM);
        }
        return RoomDetailResponseDto.builder()
                .roomMasterId(roomMaster.getRoomId())
                .roomName(roomMaster.getRoomName())
                .people(memberCount + 1) // Todo :: 수정 필요
                .build();
    }

    // 내가 구독한 채팅방 리스트 가져오기
    @Transactional(readOnly = true)
    public List<RoomMasterResponseDto> myChatRooms(UserDetails userDetails) throws RaidhoException {
        Long memberId = ((PrincipalDetails) userDetails).getMember().getId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_MEMBER));
        List<RoomMaster> roomMasterList = roomMasterRepository.findAllByRoomDetails_Member(member);
        List<RoomMasterResponseDto> roomMasterResponseDtoList = new ArrayList<>();
        for (RoomMaster rm : roomMasterList) {
            roomMasterResponseDtoList.add(
                    RoomMasterResponseDto.builder()
                            .roomMasterId(rm.getRoomId())
                            .roomName(rm.getRoomName())
                            .roomPic(rm.getRoomPic())
                            .build()
            );
        }
        return roomMasterResponseDtoList;
    }
}

package com.project.raidho.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.project.raidho.Util.RoomUtils;
import com.project.raidho.domain.chat.ChatDto.EachRoomInfoDto;
import com.project.raidho.domain.chat.ChatDto.RoomMasterRequestDto;
import com.project.raidho.domain.chat.ChatDto.RoomMasterResponseDto;
import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.chat.ChatDto.RoomDetailResponseDto;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.tags.MeetingTags;
import com.project.raidho.exception.ErrorCode;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.repository.*;
import com.project.raidho.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final MeetingTagRepository meetingTagRepository;
    private final ChatMessageRepository chatMessageRepository;

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
                .memberCount(meetingPost.getPeople())
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
    public ResponseEntity<?> joinChatRoom(Long roomId, UserDetails userDetails) throws RaidhoException {
        Long memberId = ((PrincipalDetails) userDetails).getMember().getId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_MEMBER));
        RoomMaster roomMaster = roomMasterRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 채팅방입니다."));
        int memberCount = roomDetailRepository.getCountJoinRoomMember(roomMaster);
        if (memberCount >= roomMaster.getMemberCount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("FULL"); // Todo :: 추후 error 처리 고쳐야함
        }
        RoomDetail roomDetail = roomDetailRepository.findByRoomMasterAndMember(roomMaster, member);
        if (roomDetail == null) {
            RoomDetail newRoomDetail = new RoomDetail(roomMaster, member);
            roomMaster.getRoomDetails().add(newRoomDetail);
            roomDetailRepository.save(newRoomDetail);
        } else {
            throw new RaidhoException(ErrorCode.ALREADY_JOIN_CHAT_ROOM);
        }

        RoomDetailResponseDto responseDto = RoomDetailResponseDto.builder()
                .roomMasterId(roomMaster.getRoomId())
                .roomName(roomMaster.getRoomName())
                .build();

        return ResponseEntity.ok().body(responseDto);
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

    // 단체방 정보 단건 조회
    @Transactional(readOnly = true)
    public EachRoomInfoDto eachChatRoomInfo(UserDetails userDetails, Long roomId) {
        MeetingPost meetingPost = meetingPostRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("없어방")); // Todo :: 수정해야됨
        List<MeetingTags> meetingTags = meetingTagRepository.findByMeetingPost(meetingPost);
        List<String> SmeetingTags = new ArrayList<>();
        for (MeetingTags m : meetingTags) {
            SmeetingTags.add(m.getMeetingTag());
        }
        RoomMaster roomMaster = roomMasterRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        int memberCount = roomDetailRepository.getCountJoinRoomMember(roomMaster);
        List<Long> memberId = roomDetailRepository.getAllMemberId(roomMaster);
        List<String> memberName = new ArrayList<>();
        for (Long i : memberId) {
            Member member = memberRepository.findById(i)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다.")); // Todo :: 추후 수정
            memberName.add(member.getMemberName());
        }
        return EachRoomInfoDto.builder()
                .themeCategory(meetingPost.getThemeCategory().getCountryName())
                .title(meetingPost.getTitle())
                .meetingTags(SmeetingTags)
                .memberNames(memberName)
                .startDate(meetingPost.getStartDate())
                .endDate(meetingPost.getEndDate())
                .roomCloseDate(meetingPost.getRoomCloseDate())
                .departLocation(meetingPost.getDepartLocation())
                .desc(meetingPost.getDesc())
                .people(meetingPost.getPeople())
                .memberCount(memberCount)
                .build();
    }


    // 채팅방 나가기
    @Transactional
    public void exitChatRoom(UserDetails userDetails, Long roomId) {
        Member member = ((PrincipalDetails) userDetails).getMember();
        RoomMaster roomMaster = roomMasterRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        MeetingPost meetingPost = meetingPostRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모집글입니다."));
        if (meetingPost.getMember().getProviderId().equals(member.getProviderId())) {
            chatMessageRepository.deleteAllByRoomId(roomId);
            roomDetailRepository.deleteByRoomMaster_RoomId(roomId);
            meetingPostRepository.delete(meetingPost);
        } else {
            roomDetailRepository.deleteByRoomMasterAndMember(roomMaster, member);
        }
    }
}

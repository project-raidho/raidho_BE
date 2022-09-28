package com.project.raidho.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.project.raidho.Util.RoomUtils;
import com.project.raidho.domain.chat.ChatDto.*;
import com.project.raidho.domain.chat.ChatMessage;
import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.tags.MeetingTags;
import com.project.raidho.exception.ErrorCode;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.redis.RedisPublisher;
import com.project.raidho.redis.RedisSubscriber;
import com.project.raidho.repository.*;
import com.project.raidho.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final MemberRepository memberRepository;

    private final RoomMasterRepository roomMasterRepository;

    private final MeetingPostRepository meetingPostRepository;

    private final RoomDetailRepository roomDetailRepository;

    private final MeetingTagRepository meetingTagRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final Map<String, ChannelTopic> topics;

    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

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
        log.info("{} 채팅방이 정상적으로 생성되었습니다.", meetingPost.getTitle());
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
                .orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_CHATTING_ROOM));
        int memberCount = roomDetailRepository.getCountJoinRoomMember(roomMaster);
        if (memberCount >= roomMaster.getMemberCount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RaidhoException(ErrorCode.CHATTING_ROOM_ALREADY_FULL));
        }
        RoomDetail roomDetail = roomDetailRepository.findByRoomMasterAndMember(roomMaster, member);
        if (roomDetail == null) {
            RoomDetail newRoomDetail = new RoomDetail(roomMaster, member);
            roomMaster.getRoomDetails().add(newRoomDetail);
            roomDetailRepository.save(newRoomDetail);
            log.info("{} 님께서 {} 채팅방에 참여하셨습니다.", member.getMemberName(), roomMaster.getMeetingPost().getTitle());
            ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                    .sender(member.getMemberName())
                    .message(member.getMemberName() + "님이 채팅방에 입장하셨습니다.")
                    .roomId(String.valueOf(roomId))
                    .type(ChatMessage.Type.ENTER).build();
            simpMessageSendingOperations.convertAndSend("/sub/chat/message/" + roomId, chatMessageDto);
            ChatMessage chatMessage = ChatMessage.builder().message(chatMessageDto.getMessage())
                    .type(chatMessageDto.getType()).sender(chatMessageDto.getSender()).roomId(roomId).build();
            chatMessageRepository.save(chatMessage);
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

//            Long unReadCount = 0L;
//            RoomDetail roomDetail = roomDetailRepository.findByRoomMasterAndMember(rm, member);
//            if (roomDetail.getChatId() != null) {
//                unReadCount = chatMessageRepository.countFromLastReadChat(roomDetail.getChatId());
//            }

            roomMasterResponseDtoList.add(
                    RoomMasterResponseDto.builder()
                            .roomMasterId(rm.getRoomId())
                            .roomName(rm.getRoomName())
                            .roomPic(rm.getRoomPic())
                            //   .unReadCount(unReadCount)
                            .build()
            );
        }
        return roomMasterResponseDtoList;
    }

    // 단체방 정보 단건 조회
    @Transactional(readOnly = true)
    public EachRoomInfoDto eachChatRoomInfo(UserDetails userDetails, Long roomId) throws RaidhoException {
        Boolean isMine = false;
        Member userDetailsMember = ((PrincipalDetails) userDetails).getMember();
        MeetingPost meetingPost = meetingPostRepository.findById(roomId)
                .orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_MEETING_POST));
        if (meetingPost.getMember().getProviderId().equals(userDetailsMember.getProviderId())) {
            isMine = true;
        }
        List<MeetingTags> meetingTags = meetingTagRepository.findAllByMeetingPost(meetingPost);
        List<String> SmeetingTags = new ArrayList<>();
        for (MeetingTags m : meetingTags) {
            SmeetingTags.add(m.getMeetingTag());
        }
        RoomMaster roomMaster = roomMasterRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_CHATTING_ROOM));
        int memberCount = roomDetailRepository.getCountJoinRoomMember(roomMaster);
        List<Long> memberId = roomDetailRepository.getAllMemberId(roomMaster);
        List<String> memberName = new ArrayList<>();
        for (Long i : memberId) {
            Member member = memberRepository.findById(i)
                    .orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_MEMBER));
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
                .isMine(isMine)
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
            log.info("{} 모집글이 삭제되었습니다.", meetingPost.getTitle());
        } else {
            roomDetailRepository.deleteByRoomMasterAndMember(roomMaster, member);
            log.info("{} 채팅방에서 탈퇴하셨습니다.", meetingPost.getTitle());
        }
    }

    @Transactional
    public void updateLastReadChat(Long roomId, Long memberId) {
        RoomDetail roomDetail = roomDetailRepository.findByRoomMaster_RoomIdAndMember_Id(roomId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 속해있지 않는 회원입니다."));
        ChatMessage chatMessage = chatMessageRepository.findFirstByRoomIdOrderByCreatedAtDesc(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅 내역이 존재하지 않습니다."));
        roomDetail.updateChatId(chatMessage.getId());
    }

    public void enterChatRoom(String roomId) {
        ChannelTopic topic = new ChannelTopic(roomId);
        redisMessageListener.addMessageListener(redisSubscriber, topic);
        topics.put(roomId, topic);
    }

    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }

}

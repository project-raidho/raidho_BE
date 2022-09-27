package com.project.raidho.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.meetingPost.ThemeCategory;
import com.project.raidho.domain.meetingPost.dto.MeetingPostRequestDto;
import com.project.raidho.domain.meetingPost.dto.MeetingPostResponseDto;
import com.project.raidho.domain.meetingPost.dto.UpdateMeetingPost;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.tags.MeetingTags;
import com.project.raidho.domain.ResponseDto;
import com.project.raidho.exception.ErrorCode;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.jwt.JwtTokenProvider;
import com.project.raidho.repository.*;
import com.project.raidho.security.PrincipalDetails;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@Builder
@RequiredArgsConstructor
public class MeetingPostService {

    private final MeetingPostRepository meetingPostRepository;
    private final MeetingTagRepository meetingTagRepository;
    private final ThemeCategoryRepository themeCategoryRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoomMasterRepository roomMasterRepository;
    private final RoomDetailRepository roomDetailRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ResponseDto<?> createMeetingPost(MeetingPostRequestDto meetingPostRequestDto, HttpServletRequest request) {
        Member member = validateMember(request);
        if (member == null) {
            log.error(ErrorCode.DOESNT_EXIST_MEMBER.getErrorMessage());
            throw new NullPointerException("회원만 사용 가능합니다.");
        }
        ThemeCategory themeCategory = isPresentThemeCatogory(meetingPostRequestDto);
        MeetingPost meetingPost = meetingPostRepository.save(
                MeetingPost.builder()
                        .themeCategory(themeCategory)
                        .title(meetingPostRequestDto.getTitle())
                        .desc(meetingPostRequestDto.getDesc())
                        .startDate(meetingPostRequestDto.getStartDate())
                        .endDate(meetingPostRequestDto.getEndDate())
                        .people(meetingPostRequestDto.getPeople())
                        .roomCloseDate(meetingPostRequestDto.getRoomCloseDate())
                        .departLocation(meetingPostRequestDto.getDepartLocation())
                        .member(member)
                        .build()
        );
        List<String> meetingTag = meetingPostRequestDto.getMeetingTags();
        if (meetingTag != null) {
            for (String meetingTags : meetingTag)
                meetingTagRepository.save(
                        MeetingTags.builder()
                                .meetingTag(meetingTags)
                                .meetingPost(meetingPost)
                                .build()
                );
        }
        log.info("{} 게시글이 등록되었습니다.", meetingPost.getTitle());
        return ResponseDto.success(
                MeetingPostResponseDto.builder()
                        .id(meetingPost.getId())
                        .themeCategory(themeCategory.toString())
                        .title(meetingPost.getTitle())
                        .desc(meetingPost.getDesc())
                        .departLocation(meetingPost.getDepartLocation())
                        .startDate(meetingPost.getStartDate())
                        .endDate(meetingPost.getEndDate())
                        .people(meetingPost.getPeople())
                        .roomCloseDate(meetingPost.getRoomCloseDate())
                        .meetingTags(meetingTag)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<?> updateMeetingPost(Long meetingId, UserDetails userDetails, UpdateMeetingPost updateMeetingPost) throws RaidhoException {
        Member member = new Member();
        MeetingPost meetingPost = meetingPostRepository.findById(meetingId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_MEETING_POST));
        RoomMaster roomMaster = roomMasterRepository.findByRoomId(meetingId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_CHATTING_ROOM));
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        if (member.getProviderId() != null) {
            if (member.getProviderId().equals(meetingPost.getMember().getProviderId())) {
                meetingPost.updateMeetingPost(updateMeetingPost);
                roomMaster.updateRoomMaster(updateMeetingPost);
                log.info("{} 모집글 수정이 완료되었습니다.", meetingPost.getTitle());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RaidhoException(ErrorCode.INVALID_AUTH_MEMBER_UPDATE));
        }
        return ResponseEntity.ok().body("정상적으로 수정되었습니다.");
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllMyMeetingPost(UserDetails userDetails) throws ParseException {
        if (userDetails != null) {
            Member member = ((PrincipalDetails) userDetails).getMember();
            if (member != null) {
                List<MeetingPost> meetingPostList = meetingPostRepository.findAllByMember_IdOrderByCreatedAtDesc(member.getId());
                return ResponseEntity.ok().body(ResponseDto.success(convertToMyPageResponseDto(meetingPostList, userDetails)));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RaidhoException(ErrorCode.DOESNT_EXIST_MEMBER));
    }

    private List<MeetingPostResponseDto> convertToMyPageResponseDto(List<MeetingPost> meetingPostList, UserDetails userDetails) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        Boolean isMine = false;
        Boolean isAlreadyJoin = false;
        int meetingStatus = 0;
        List<MeetingPostResponseDto> meetingPosts = new ArrayList<>();
        for (MeetingPost meetingPost : meetingPostList) {
            RoomMaster roomMaster = roomMasterRepository.findByRoomId(meetingPost.getId())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 채팅방입니다."));
            int memberCount = roomDetailRepository.getCountJoinRoomMember(roomMaster);
            if (member.getProviderId() != null) {
                if (member.getProviderId().equals(meetingPost.getMember().getProviderId())) {
                    isMine = true;
                }
                RoomDetail roomDetails = roomDetailRepository.findByRoomMasterAndMember(roomMaster, member);
                if (roomDetails != null) {
                    isAlreadyJoin = true;
                }
            }
            Date date = formatter.parse(meetingPost.getRoomCloseDate());
            if (date.after(new Date()) && (meetingPost.getPeople() > memberCount)) {
                meetingStatus = 1;
            } else if (date.after(new Date()) && memberCount >= meetingPost.getPeople()) {
                meetingStatus = 2;
            } else if (date.before(new Date())) {
                meetingStatus = 3;
            }
            List<MeetingTags> meetingTags = meetingTagRepository.findAllByMeetingPost(meetingPost);
            List<String> stringTagList = new ArrayList<>();
            for (MeetingTags mt : meetingTags) {
                stringTagList.add(mt.getMeetingTag());
            }
            meetingPosts.add(
                    MeetingPostResponseDto.builder()
                            .id(meetingPost.getId())
                            .themeCategory(meetingPost.getThemeCategory().getCountryName())
                            .title(meetingPost.getTitle())
                            .desc(meetingPost.getDesc())
                            .departLocation(meetingPost.getDepartLocation())
                            .startDate(meetingPost.getStartDate())
                            .endDate(meetingPost.getEndDate())
                            .people(meetingPost.getPeople())
                            .memberCount(memberCount)
                            .roomCloseDate(meetingPost.getRoomCloseDate())
                            .isMine(isMine)
                            .isAlreadyJoin(isAlreadyJoin)
                            .meetingTags(stringTagList)
                            .meetingStatus(meetingStatus)
                            .memberName(meetingPost.getMember().getMemberName())
                            .memberImage(meetingPost.getMember().getMemberImage())
                            .build()
            );

            isMine = false;
            isAlreadyJoin = false;
        }
        return meetingPosts;
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllMeetingPost(int page, int size, UserDetails userDetails) throws ParseException {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<MeetingPost> meetingPostList = meetingPostRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        Page<MeetingPostResponseDto> meetingPostResponseDtos = convertToBasicResponseDto(meetingPostList, userDetails);
        return ResponseDto.success(meetingPostResponseDtos);
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllCategoryMeetingPost(int page, int size, UserDetails userDetails, String theme) throws ParseException, RaidhoException {
        PageRequest pageRequest = PageRequest.of(page, size);
        ThemeCategory category = themeCategoryRepository.findByCountryName(theme)
                .orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_CATEGORY));
        Page<MeetingPost> meetingPostList = meetingPostRepository.findAllByThemeCategory_IdOrderByCreatedAtDesc(category.getId(), pageRequest);
        Page<MeetingPostResponseDto> meetingPostResponseDtos = convertToBasicResponseDto(meetingPostList, userDetails);
        return ResponseDto.success(meetingPostResponseDtos);
    }

    // Todo ::
    @Transactional(readOnly = true)
    public ResponseDto<?> getRoomCloseDate(int page, int size, UserDetails userDetails, int num) throws ParseException {
        PageRequest pageRequest = PageRequest.of(page, size);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (num == 1) {
            System.out.println("09803948230948230948302948320948230948230948230948230948230498");
            Page<MeetingPost> meetingPosts = meetingPostRepository.getRoomCloseDateOpen(date, pageRequest);
            System.out.println("dofmdsoidsoifcjdpsoifjcs[d,fij[sdpifcj,[dspfc,j[sdpf,jc[dpsif,jc[pi");
            Page<MeetingPostResponseDto> meetingPostResponseDtos = convertToRoomCloseDate(meetingPosts, userDetails);
            return ResponseDto.success(meetingPostResponseDtos);
        }
        return null;
    }

    private Page<MeetingPostResponseDto> convertToRoomCloseDate(Page<MeetingPost> meetingPosts, UserDetails userDetails) {
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        Boolean isMine = false;
        Boolean isAlreadyJoin = false;

        List<MeetingPostResponseDto> meetingPostList = new ArrayList<>();
        for (MeetingPost meetingPost : meetingPosts) {
            RoomMaster roomMaster = roomMasterRepository.findByRoomId(meetingPost.getId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
            int memberCount = roomDetailRepository.getCountJoinRoomMember(roomMaster);
            if (member.getProviderId() != null) {
                if (member.getProviderId().equals(meetingPost.getMember().getProviderId())) {
                    isMine = true;
                }
                RoomDetail roomDetails = roomDetailRepository.findByRoomMasterAndMember(roomMaster, member);
                if (roomDetails != null) {
                    isAlreadyJoin = true;
                }
            }
            List<MeetingTags> meetingTags = meetingTagRepository.findAllByMeetingPost(meetingPost);
            List<String> stringTagList = new ArrayList<>();
            for (MeetingTags mt : meetingTags) {
                stringTagList.add(mt.getMeetingTag());
            }
            if (meetingPost.getPeople() > memberCount) {
                meetingPostList.add(
                        MeetingPostResponseDto.builder()
                                .id(meetingPost.getId())
                                .themeCategory(meetingPost.getThemeCategory().getCountryName())
                                .title(meetingPost.getTitle())
                                .desc(meetingPost.getDesc())
                                .departLocation(meetingPost.getDepartLocation())
                                .startDate(meetingPost.getStartDate())
                                .endDate(meetingPost.getEndDate())
                                .people(meetingPost.getPeople())
                                .memberCount(memberCount)
                                .roomCloseDate(meetingPost.getRoomCloseDate())
                                .isMine(isMine)
                                .isAlreadyJoin(isAlreadyJoin)
                                .meetingTags(stringTagList)
                                .meetingStatus(1)
                                .memberName(meetingPost.getMember().getMemberName())
                                .memberImage(meetingPost.getMember().getMemberImage())
                                .build()
                );
            }
            isMine = false;
            isAlreadyJoin = false;
        }
        return new PageImpl<>(meetingPostList, meetingPosts.getPageable(), meetingPosts.getTotalElements());
    }

    private Page<MeetingPostResponseDto> convertToBasicResponseDto(Page<MeetingPost> meetingPostList, UserDetails userDetails) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        Boolean isMine = false;
        Boolean isAlreadyJoin = false;
        int meetingStatus = 0;
        List<MeetingPostResponseDto> meetingPosts = new ArrayList<>();
        for (MeetingPost meetingPost : meetingPostList) {
            RoomMaster roomMaster = roomMasterRepository.findByRoomId(meetingPost.getId())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 채팅방입니다."));
            int memberCount = roomDetailRepository.getCountJoinRoomMember(roomMaster);
            if (member.getProviderId() != null) {
                if (member.getProviderId().equals(meetingPost.getMember().getProviderId())) {
                    isMine = true;
                }
                RoomDetail roomDetails = roomDetailRepository.findByRoomMasterAndMember(roomMaster, member);
                if (roomDetails != null) {
                    isAlreadyJoin = true;
                }
            }
            Date date = formatter.parse(meetingPost.getRoomCloseDate());
            if (date.after(new Date()) && (meetingPost.getPeople() > memberCount)) {
                meetingStatus = 1;
            } else if (date.after(new Date()) && memberCount >= meetingPost.getPeople()) {
                meetingStatus = 2;
            } else if (date.before(new Date())) {
                meetingStatus = 3;
            }
            List<MeetingTags> meetingTags = meetingTagRepository.findAllByMeetingPost(meetingPost);
            List<String> stringTagList = new ArrayList<>();
            for (MeetingTags mt : meetingTags) {
                stringTagList.add(mt.getMeetingTag());
            }
            meetingPosts.add(
                    MeetingPostResponseDto.builder()
                            .id(meetingPost.getId())
                            .themeCategory(meetingPost.getThemeCategory().getCountryName())
                            .title(meetingPost.getTitle())
                            .desc(meetingPost.getDesc())
                            .departLocation(meetingPost.getDepartLocation())
                            .startDate(meetingPost.getStartDate())
                            .endDate(meetingPost.getEndDate())
                            .people(meetingPost.getPeople())
                            .memberCount(memberCount)
                            .roomCloseDate(meetingPost.getRoomCloseDate())
                            .isMine(isMine)
                            .isAlreadyJoin(isAlreadyJoin)
                            .meetingTags(stringTagList)
                            .meetingStatus(meetingStatus)
                            .memberName(meetingPost.getMember().getMemberName())
                            .memberImage(meetingPost.getMember().getMemberImage())
                            .build()
            );
            isMine = false;
            isAlreadyJoin = false;
        }
        return new PageImpl<>(meetingPosts, meetingPostList.getPageable(), meetingPostList.getTotalElements());
    }

    @Transactional
    public ResponseDto<?> deleteMeetingPost(Long meetingId, UserDetails userDetails) throws RaidhoException {
        MeetingPost meetingPost = meetingPostRepository.findById(meetingId).orElse(null);
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        if (meetingPost == null) {
            log.error(ErrorCode.DOESNT_EXIST_MEETING_POST.getErrorMessage());
            throw new RaidhoException(ErrorCode.DOESNT_EXIST_MEETING_POST);
        }
        if (!member.getProviderId().equals(meetingPost.getMember().getProviderId())) {
            log.error(ErrorCode.UNAUTHORIZATION_MEMBER.getErrorMessage());
            throw new RaidhoException(ErrorCode.UNAUTHORIZATION_MEMBER);
        } else {
            chatMessageRepository.deleteAllByRoomId(meetingId);
            roomDetailRepository.deleteByRoomMaster_RoomId(meetingId);
            meetingPostRepository.delete(meetingPost);
            log.info("{} 모집글이 정삭적으로 삭제되었습니다.", meetingPost.getTitle());
            return ResponseDto.success("모집글이 정상적으로 삭제되었습니다.");
        }
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        String accessToken = resolveToken(request.getHeader("Authorization"));

        if (!jwtTokenProvider.validationToken(accessToken)) {
            return null;
        }
        return jwtTokenProvider.getMemberFromAuthentication();
    }

    private String resolveToken(String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }
        throw new RuntimeException("NOT VALID ACCESS TOKEN");
    }

    @Transactional(readOnly = true)
    public ThemeCategory isPresentThemeCatogory(MeetingPostRequestDto requestDto) {
        Optional<ThemeCategory> themeCategory = themeCategoryRepository.findByCountryName(requestDto.getThemeCategory());
        return themeCategory.orElseThrow(() -> new IllegalArgumentException("등록되지 않은 카테고리입니다."));
    }

}

package com.project.raidho.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.meetingPost.ThemeCategory;
import com.project.raidho.domain.meetingPost.dto.MeetingPostRequestDto;
import com.project.raidho.domain.meetingPost.dto.MeetingPostResponseDto;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.Post;
import com.project.raidho.domain.tags.MeetingTags;
import com.project.raidho.domain.ResponseDto;
import com.project.raidho.exception.ErrorCode;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.jwt.JwtTokenProvider;
import com.project.raidho.repository.*;
import com.project.raidho.security.PrincipalDetails;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    public ResponseDto<?> createMeetingPost(MeetingPostRequestDto meetingPostRequestDto, HttpServletRequest request) throws IOException {

        // 회원정보 확인 로직
        Member member = validateMember(request);

        if (member == null) {
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
                        .people(meetingPostRequestDto.getPeople())
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

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllMeetingPost (int page, int size, UserDetails userDetails) throws ParseException {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<MeetingPost> meetingPostList = meetingPostRepository.findAllByOrderByCreatedAtDesc(pageRequest);

        Page<MeetingPostResponseDto> meetingPostResponseDtos = convertToBasicResponseDto(meetingPostList, userDetails);

        return ResponseDto.success(meetingPostResponseDtos);
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllCategoryMeetingPost (int page, int size, UserDetails userDetails, String theme) throws ParseException {
        PageRequest pageRequest = PageRequest.of(page, size);
        ThemeCategory category = themeCategoryRepository.findByCountryName(theme)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리는 존재하지 않습니다."));
        Page<MeetingPost> meetingPostList = meetingPostRepository.findAllByThemeCategory_IdOrderByCreatedAtDesc(category.getId(), pageRequest);
        Page<MeetingPostResponseDto> meetingPostResponseDtos = convertToBasicResponseDto(meetingPostList, userDetails);
        return ResponseDto.success(meetingPostResponseDtos);
    }

    private Page<MeetingPostResponseDto> convertToBasicResponseDto (Page<MeetingPost> meetingPostList,  UserDetails userDetails) throws ParseException {

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

            if (date.after(new Date()) && (meetingPost.getPeople() > memberCount )) {
                meetingStatus = 1;
            }
            else if (date.after(new Date()) && memberCount >= meetingPost.getPeople()) {
                meetingStatus = 2;
            }
            else if (date.before(new Date())) {
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
                            .meetingParticipant(1)
                            .meetingStatus(meetingStatus) // Todo ;; 모집중인지 아닌지..
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
    public ResponseDto<?> deleteMeetingPost (Long meetingId, UserDetails userDetails){
        MeetingPost meetingPost = meetingPostRepository.findById(meetingId).orElse(null);
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        if (meetingPost == null) {
            throw new NullPointerException("존재하지 않는 게시글 입니다.");
        }
        if (!member.getProviderId().equals(meetingPost.getMember().getProviderId())) {
            throw new NullPointerException("게시글 주인이 아닙니다.");
        } else {
            chatMessageRepository.deleteAllByRoomId(meetingId);
            roomDetailRepository.deleteByRoomMaster_RoomId(meetingId);
            meetingPostRepository.delete(meetingPost);
            return ResponseDto.success("구인 구직 글 삭제 성공");
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
    private String resolveToken (String accessToken){
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

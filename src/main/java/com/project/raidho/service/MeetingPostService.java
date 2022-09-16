package com.project.raidho.service;

import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.meetingPost.ThemeCategory;
import com.project.raidho.domain.meetingPost.dto.MeetingPostRequestDto;
import com.project.raidho.domain.meetingPost.dto.MeetingPostResponseDto;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.tags.MeetingTags;
import com.project.raidho.domain.ResponseDto;
import com.project.raidho.jwt.JwtTokenProvider;
import com.project.raidho.repository.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Builder
@RequiredArgsConstructor
public class MeetingPostService {
    private final MeetingRepository meetingRepository;
    private final MeetingTagRepository meetingTagRepository;
    private final CategoryRepository categoryRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ResponseDto<?> createMeetingPost(MeetingPostRequestDto meetingPostRequestDto, HttpServletRequest request) throws IOException {

        // 회원정보 확인 로직
        Member member = validateMember(request);

        if (member == null) {
            throw new NullPointerException("회원만 사용 가능합니다.");
        }

        ThemeCategory themeCategory = isPresentThemeCatogory(meetingPostRequestDto);

        MeetingPost meetingPost = meetingRepository.save(
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
        Optional<ThemeCategory> themeCategory = categoryRepository.findByCountryName(requestDto.getThemeCategory());
        return themeCategory.orElseThrow(() -> new IllegalArgumentException("등록되지 않은 카테고리입니다."));
    }
}

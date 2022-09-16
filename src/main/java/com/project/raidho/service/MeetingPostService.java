package com.project.raidho.service;

import com.project.raidho.domain.*;
import com.project.raidho.domain.locationTags.LocationTags;
import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.meetingPost.dto.MeetingPostRequestDto;
import com.project.raidho.domain.meetingPost.dto.MeetingResponseDto;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.member.dto.MembersResponseDto;
import com.project.raidho.domain.post.Post;
import com.project.raidho.domain.post.dto.PostRequestDto;
import com.project.raidho.domain.post.dto.UpdatePostRequestDto;
import com.project.raidho.domain.post.dto.PostResponseDto;
import com.project.raidho.domain.s3.MultipartFiles;
import com.project.raidho.domain.tags.MeetingTags;
import com.project.raidho.domain.tags.Tags;
import com.project.raidho.domain.ResponseDto;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Builder
@RequiredArgsConstructor
public class MeetingPostService {
    private final MeetingRepository meetingRepository;
    private final MeetingTagRepository meetingTagRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ResponseDto<?> createMeetingPost(MeetingPostRequestDto meetingPostRequestDto, UserDetails userDetails) throws IOException {

        // 회원정보 확인 로직
        Member member = new Member();

        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }

        MeetingPost meetingPost = meetingRepository.save(
                MeetingPost.builder()

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
                MeetingResponseDto.builder()
                        .id(meetingPost.getId())
                        .title(meetingPost.getTitle())
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
}

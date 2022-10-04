package com.project.raidho.service;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.meetingPost.dto.MeetingPostResponseDto;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.Post;
import com.project.raidho.domain.post.dto.MainPostResponseDto;
import com.project.raidho.repository.*;
import com.project.raidho.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;

@RequiredArgsConstructor
@Service
public class TagSearchService {

    private final TagRepository tagRepository;
    private final MeetingTagRepository meetingTagRepository;
    private final ServiceProvider serviceProvider;

    @Transactional(readOnly = true)
    public ResponseDto<?> searchTag(int page, int size, String tag, UserDetails userDetails) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Post> postList = tagRepository.SearchTag(tag, pageRequest);
        Page<MainPostResponseDto> mainPostResponseDtos = convertToMainPostResponseDto(postList, userDetails);
        return ResponseDto.success(mainPostResponseDtos);
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> distinctMyPostSearchTag(int page, int size, String tag, Long id, UserDetails userDetails) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Post> postPage = tagRepository.distinctMyPostSearchTag(tag ,id ,pageRequest);
        Page<MainPostResponseDto> mainPostResponseDtoPage = convertToMainPostResponseDto(postPage, userDetails);
        return ResponseDto.success(mainPostResponseDtoPage);
    }

    // Todo :: pagenation 처리
    private Page<MainPostResponseDto> convertToMainPostResponseDto(Page<Post> postList, UserDetails userDetails) {
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }

        return new PageImpl<>(serviceProvider.postPage(postList,member), postList.getPageable(), postList.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> meetingTagSearch(int page, int size, String meetingTag, UserDetails userDetails) throws ParseException {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<MeetingPost> meetingPostList = meetingTagRepository.SearchMeetingTag(meetingTag, pageRequest);
        Page<MeetingPostResponseDto> mainPostResponseDto = convertToMainMeetingPostResponseDto(meetingPostList, userDetails);
        return ResponseDto.success(mainPostResponseDto);
    }

    private Page<MeetingPostResponseDto> convertToMainMeetingPostResponseDto(Page<MeetingPost> meetingPostList, UserDetails userDetails) throws ParseException {
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        return new PageImpl<>(serviceProvider.meetingPostPage(meetingPostList, member), meetingPostList.getPageable(), meetingPostList.getTotalElements());
    }

}

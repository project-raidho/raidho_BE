package com.project.raidho.service;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.Post;
import com.project.raidho.domain.post.dto.PostResponseDto;
import com.project.raidho.domain.s3.MultipartFiles;
import com.project.raidho.repository.ImgRepository;
import com.project.raidho.repository.PostHeartRepository;
import com.project.raidho.repository.PostRepository;
import com.project.raidho.repository.TagRepository;
import com.project.raidho.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TagSearchService {

    private final PostRepository postRepository;

    private final TagRepository tagRepository;
    private final PostHeartRepository postHeartRepository;
    private final ImgRepository imgRepository;

    @Transactional(readOnly = true)
    public ResponseDto<?> searchTag(int page, int size, String tag, UserDetails userDetails) {

        PageRequest pageRequest = PageRequest.of(page,size);
        Page<Post> postList = tagRepository.SearchTag(tag, pageRequest);
        Page<PostResponseDto> postResponseDtos = convertToBasicResponseDto(postList, userDetails);
        return ResponseDto.success(postResponseDtos);
    }

    // Todo :: pagenation 처리
    private Page<PostResponseDto> convertToBasicResponseDto (Page <Post> postList, UserDetails userDetails){

        Member member = new Member();

        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }

        Boolean isMine = false;
        Boolean isHeartMine = false;

        List<PostResponseDto> posts = new ArrayList<>();
        for (Post post : postList) {
            if (member.getProviderId() != null) {
                if (member.getProviderId().equals(post.getMember().getProviderId())) {
                    isMine = true;
                }
            }

            int heartCount = postHeartRepository.getCountOfPostHeart(post);
            if (member.getProviderId() != null) {
                int isHeartMineCh = postHeartRepository.getCountOfPostAndMemberPostHeart(post, member);
                if (isHeartMineCh >= 1) {
                    isHeartMine = true;
                }
            }


            List<MultipartFiles> multipartFile = imgRepository.findAllByPost_Id(post.getId());
            List<String> multipartFiles = new ArrayList<>();
            for (MultipartFiles c : multipartFile) {
                multipartFiles.add(c.getMultipartFiles());
            }

            posts.add(
                    PostResponseDto.builder()
                            .id(post.getId())
                            .memberName(post.getMember().getMemberName())
                            .memberImage(post.getMember().getMemberImage())
                            .content(post.getContent())
                            .multipartFiles(multipartFiles)
                            .heartCount(heartCount)
                            .isMine(isMine)
                            .isHeartMine(isHeartMine)
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
                            .build()
            );
            isMine = false;
            isHeartMine = false;
        }
        return new PageImpl<>(posts, postList.getPageable(), postList.getTotalElements());
    }

}

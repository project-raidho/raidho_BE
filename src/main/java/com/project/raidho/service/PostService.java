package com.project.raidho.service;

import com.project.raidho.domain.*;
import com.project.raidho.domain.member.Member;
import com.project.raidho.dto.request.PostRequestDto;
import com.project.raidho.dto.resposnse.PostResponseDto;
import com.project.raidho.dto.resposnse.ResponseDto;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Builder
@RequiredArgsConstructor
public class PostService extends Timestamped {
    private final PostRepository postRepository;
    private final ImgRepository imgRepository;
    private final TagRepository tagRepository;
    private final PostHeartRepository postHeartRepository;
    private final S3Service s3Service;
    private final LocationTagsRepository locationTagsRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // Todo :: 게시물 업로드
    @Transactional
    public ResponseDto<?> createPost(PostRequestDto postRequestDto, HttpServletRequest request) throws IOException {

        // 회원정보 확인 로직
        Member member = validateMember(request);

        if (member == null) {
            throw new NullPointerException("회원만 사용 가능합니다.");
        }

        Post post = postRepository.save(
                Post.builder()
                        .member(member)
                        .content(postRequestDto.getContent())
                        .build()
        );

        List<MultipartFile> FilesList = postRequestDto.getImgUrl();
        if (FilesList != null) {
            for (MultipartFile file : FilesList) {
                String url = s3Service.upload(file);
                imgRepository.save(
                        MultipartFiles.builder()
                                .multipartFiles(url)
                                .post(post)
                                .build()
                );
            }
        }
        List<String> tags = postRequestDto.getTags();
        if (tags != null) {
            for (String tag : tags)
                tagRepository.save(
                        Tags.builder()
                                .tag(tag)
                                .post(post)
                                .build()
                );
        }

        List<String> locationTag = postRequestDto.getLocationTags();
        if (locationTag != null) {
            for (String locationTags : locationTag)
                locationTagsRepository.save(
                        LocationTags.builder()
                                .locationTags(locationTags)
                                .post(post)
                                .build()
                );
        }
        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .content(post.getContent())
//                        .author(membersDto)
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );
    }


    // Todo :: 게시글 최신순 전체 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost(int page, int size, UserDetails userDetails) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageRequest);

        Page<PostResponseDto> postResponseDtos = convertToBasicResponseDto(postList,userDetails);

        return ResponseDto.success(postResponseDtos);

    }

    // Todo :: 게시글 좋아요 순 전체조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAlllikePost(int page, int size, UserDetails userDetails) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageRequest);

        Page<PostResponseDto> postResponseDtos = convertToBasicResponseDto(postList,userDetails);

        return ResponseDto.success(postResponseDtos);

    }
    // Todo :: pagenation 처리
    private Page<PostResponseDto> convertToBasicResponseDto(Page<Post> postList, UserDetails userDetails) {

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
            int isHeartMineCh = postHeartRepository.getCountOfPostAndMemberPostHeart(post, member);
            if (isHeartMineCh >= 1) {
                isHeartMine = true;
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

    // Todo :: accessToken validation
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        String accessToken = resolveToken(request.getHeader("Authorization"));

        if (!jwtTokenProvider.validationToken(accessToken)) {
            return null;
        }
        return jwtTokenProvider.getMemberFromAuthentication();
    }

    // Todo :: Authorization 에서 받아온 accessToken bearer 제거
    private String resolveToken(String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }
        throw new RuntimeException("NOT VALID ACCESS TOKEN");
    }
}




package com.project.raidho.service;

import com.project.raidho.domain.*;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.Post;
import com.project.raidho.domain.post.dto.MainPostResponseDto;
import com.project.raidho.domain.post.dto.PostRequestDto;
import com.project.raidho.domain.post.dto.UpdatePostRequestDto;
import com.project.raidho.domain.post.dto.PostResponseDto;
import com.project.raidho.domain.s3.MultipartFiles;
import com.project.raidho.domain.tags.Tags;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@Builder
@RequiredArgsConstructor
@Service
public class PostService extends Timestamped {
    private final PostRepository postRepository;
    private final ImgRepository imgRepository;
    private final TagRepository tagRepository;
    private final PostHeartRepository postHeartRepository;
    private final S3Service s3Service;
    private final JwtTokenProvider jwtTokenProvider;

    // 자랑글 등록
    @Transactional
    public ResponseDto<?> createPost(PostRequestDto postRequestDto, HttpServletRequest request) throws RaidhoException, IOException {
        Member member = validateMember(request);
        if (member == null) {
            log.error(ErrorCode.UNAUTHORIZATION_MEMBER.getErrorMessage());
            throw new RaidhoException(ErrorCode.UNAUTHORIZATION_MEMBER);
        }
        Post post = postRepository.save(
                Post.builder().member(member).content(postRequestDto.getContent()).build()
        );
        List<MultipartFile> FilesList = postRequestDto.getImgUrl();
        if (FilesList != null) {
            for (MultipartFile file : FilesList) {
                String url = s3Service.upload(file);
                imgRepository.save(MultipartFiles.builder().multipartFiles(url).post(post).build());
            }
        }
        List<String> tags = postRequestDto.getTags();
        if (tags != null) {
            for (String tag : tags)
                tagRepository.save(Tags.builder().tag(tag).post(post).build()
                );
        }
        return ResponseDto.success(post.getId());
    }

    // 자랑글 수정
    @Transactional
    public ResponseEntity<?> updatePost(Long postId, UserDetails userDetails, UpdatePostRequestDto updatePostRequestDto) throws RaidhoException {
        Member member = new Member();
        Post post = postRepository.findById(postId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_POST));
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        if (member.getProviderId() != null) {
            if (member.getProviderId().equals(post.getMember().getProviderId())) {
                tagRepository.deleteAllByPost_Id(post.getId());
                post.updatePost(updatePostRequestDto);
                List<String> tags = updatePostRequestDto.getTags();
                if (tags != null) {
                    for (String tag : tags)
                        tagRepository.save(
                                Tags.builder().tag(tag).post(post).build()
                        );
                }
                log.info("{} 님의 게시글이 정삭적으로 수정되었습니다.", member.getMemberName());
            }
        } else {
            log.error(ErrorCode.UNAUTHORIZATION_MEMBER.getErrorMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RaidhoException(ErrorCode.INVALID_AUTH_MEMBER_DELETE));
        }
        return ResponseEntity.ok().body("게시글이 정상적으로 수정되었습니다.");
    }

    // 게시글 최신순 전체 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost(int page, int size, UserDetails userDetails) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        Page<MainPostResponseDto> mainPostResponseDtos = convertToMainPostResponseDto(postList, userDetails);
        return ResponseDto.success(mainPostResponseDtos);
    }

    // 게시글 좋아요 순 전체조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAlllikePost(int page, int size, UserDetails userDetails) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Post> postList = postRepository.findAllByOrderByHeartCountDesc(pageRequest);
        Page<MainPostResponseDto> postResponseDtos = convertToMainPostResponseDto(postList, userDetails);
        return ResponseDto.success(postResponseDtos);
    }

    // Todo :: 게시글 단건 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getPostDetail(UserDetails userDetails, Long postId) throws RaidhoException {
        Boolean isMine = false;
        Boolean isHeartMine = false;
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        Post post = postRepository.findById(postId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_POST));
        List<Tags> tag = tagRepository.findAllByPost_Id(post.getId());
        List<String> tags = new ArrayList<>();
        for (Tags t : tag) {
            tags.add(t.getTag());
        }
        List<MultipartFiles> multipartFile = imgRepository.findAllByPost_Id(post.getId());
        List<String> multipartFiles = new ArrayList<>();
        for (MultipartFiles m : multipartFile) {
            multipartFiles.add(m.getMultipartFiles());
        }
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
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
        PostResponseDto postResponseDto = PostResponseDto.builder()
                .content(post.getContent())
                .id(post.getId())
                .memberName(post.getMember().getMemberName())
                .memberImage(post.getMember().getMemberImage())
                .tags(tags)
                .heartCount(heartCount)
                .isMine(isMine)
                .isHeartMine(isHeartMine)
                .createdAt(post.getCreatedAt().toLocalDate())
                .modifiedAt(post.getModifiedAt().toLocalDate())
                .multipartFiles(multipartFiles)
                .build();
        postResponseDtoList.add(postResponseDto);
        return ResponseDto.success(postResponseDtoList);
    }

    // Todo :: 내가 쓴글 조회 하기
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllMyPost(UserDetails userDetails) {
        if (userDetails != null) {
            Member member = ((PrincipalDetails) userDetails).getMember();
            if (member != null) {
                List<Post> postList = postRepository.findAllByMember_IdOrderByCreatedAtDesc(member.getId());
                return ResponseEntity.ok().body(ResponseDto.success(convertToMyPageResponseDto(postList, userDetails)));
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RaidhoException(ErrorCode.DOESNT_EXIST_MEMBER));
    }


    // Todo :: 게시글 삭제
    @Transactional
    public ResponseDto<?> deletePost(Long postId, UserDetails userDetails) throws RaidhoException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_POST));
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        if (!member.getProviderId().equals(post.getMember().getProviderId())) {
            throw new RaidhoException(ErrorCode.INVALID_AUTH_MEMBER_DELETE);
        } else {
            postRepository.delete(post);
            return ResponseDto.success("게시글이 정상적으로 삭제되었습니다.");
        }
    }

    private Page<MainPostResponseDto> convertToMainPostResponseDto(Page<Post> postList, UserDetails userDetails) {
        Boolean isMine = false;
        Boolean isHeartMine = false;
        Boolean isImages = false;
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        List<MainPostResponseDto> posts = new ArrayList<>();
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
            if (multipartFile.size() > 1) {
                isImages = true;
            }
            List<String> multipartFiles = new ArrayList<>();
            for (MultipartFiles c : multipartFile) {
                multipartFiles.add(c.getMultipartFiles());
            }
            posts.add(
                    MainPostResponseDto.builder()
                            .id(post.getId())
                            .memberName(post.getMember().getMemberName())
                            .memberImage(post.getMember().getMemberImage())
                            .multipartFiles(Collections.singletonList(multipartFiles.get(0)))
                            .heartCount(heartCount)
                            .isMine(isMine)
                            .isHeartMine(isHeartMine)
                            .isImages(isImages)
                            .createdAt(post.getCreatedAt().toLocalDate())
                            .modifiedAt(post.getModifiedAt().toLocalDate())
                            .build()
            );
            isMine = false;
            isHeartMine = false;
            isImages = false;
        }
        return new PageImpl<>(posts, postList.getPageable(), postList.getTotalElements());
    }

    private List<MainPostResponseDto> convertToMyPageResponseDto(List<Post> postList, UserDetails userDetails) {
        Boolean isMine = false;
        Boolean isHeartMine = false;
        Boolean isImages = false;
        Member member = new Member();
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }
        List<MainPostResponseDto> posts = new ArrayList<>();
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
            if (multipartFile.size() > 1) {
                isImages = true;
            }
            List<String> multipartFiles = new ArrayList<>();
            for (MultipartFiles c : multipartFile) {
                multipartFiles.add(c.getMultipartFiles());
            }
            posts.add(
                    MainPostResponseDto.builder()
                            .id(post.getId())
                            .memberName(post.getMember().getMemberName())
                            .memberImage(post.getMember().getMemberImage())
                            .multipartFiles(Collections.singletonList(multipartFiles.get(0)))
                            .heartCount(heartCount)
                            .isMine(isMine)
                            .isHeartMine(isHeartMine)
                            .isImages(isImages)
                            .createdAt(post.getCreatedAt().toLocalDate())
                            .modifiedAt(post.getModifiedAt().toLocalDate())
                            .build()
            );
            isMine = false;
            isHeartMine = false;
            isImages = false;
        }
        return posts;
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




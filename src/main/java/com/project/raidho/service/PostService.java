package com.project.raidho.service;

import com.project.raidho.domain.*;
import com.project.raidho.domain.locationTags.LocationTags;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.Post;
import com.project.raidho.domain.post.dto.PostRequestDto;
import com.project.raidho.domain.post.dto.UpdatePostRequestDto;
import com.project.raidho.domain.post.dto.PostResponseDto;
import com.project.raidho.domain.s3.MultipartFiles;
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
public class PostService extends Timestamped {
    private final PostRepository postRepository;
    private final ImgRepository imgRepository;
    private final TagRepository tagRepository;
    private final PostHeartRepository postHeartRepository;
    private final S3Service s3Service;
    private final LocationTagsRepository locationTagsRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

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
                        .createdAt(post.getCreatedAt().toLocalDate())
                        .modifiedAt(post.getModifiedAt().toLocalDate())
                        .build()
        );
    }

    // Todo :: 게시글 수정
    @Transactional
    public ResponseDto<?> updatePost(Long postId, UserDetails userDetails, UpdatePostRequestDto updatePostRequestDto) {

        Member member = new Member();
        Post post = postRepository.findById(postId).orElse(null);
        tagRepository.deleteAllByPost_Id(post.getId());
        locationTagsRepository.deleteAllByPost_Id(post.getId());
        //LocationTags locationTag = locationTagsRepository.findById(postId).orElse(null);

        if (post == null) {
            throw new NullPointerException("존재하지 않는 게시글 입니다..");
        }
        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }

        if (member.getProviderId() != null) {
            if (member.getProviderId().equals(post.getMember().getProviderId())) {
                post.updatePost(updatePostRequestDto);

                List<String> tags = updatePostRequestDto.getTags();
                if (tags != null) {
                    for (String tag : tags)
                        tagRepository.save(
                                Tags.builder()
                                        .tag(tag)
                                        .post(post)
                                        .build()
                        );
                }
                List<String> locationTags = updatePostRequestDto.getLocationTags();
                if (locationTags != null) {
                    for (String locationTag : locationTags)
                        locationTagsRepository.save(
                                LocationTags.builder()
                                        .locationTags(locationTag)
                                        .post(post)
                                        .build()
                        );
                }
            }
        }
        return ResponseDto.success("update success");
    }
        // Todo :: 게시글 최신순 전체 조회
        @Transactional(readOnly = true)
        public ResponseDto<?> getAllPost ( int page, int size, UserDetails userDetails){

            PageRequest pageRequest = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageRequest);

            Page<PostResponseDto> postResponseDtos = convertToBasicResponseDto(postList, userDetails);

            return ResponseDto.success(postResponseDtos);

        }

        // Todo :: 게시글 좋아요 순 전체조회
        @Transactional(readOnly = true)
        public ResponseDto<?> getAlllikePost ( int page, int size, UserDetails userDetails){

            PageRequest pageRequest = PageRequest.of(page, size);

            Page<Post> postList = postRepository.findAllByOrderByHeartCountDesc(pageRequest);

            Page<PostResponseDto> postResponseDtos = convertToBasicResponseDto(postList, userDetails);

            return ResponseDto.success(postResponseDtos);

        }

        // Todo :: 게시글 단건 조회
        @Transactional(readOnly = true)
        public ResponseDto<?> getPostDetail (UserDetails userDetails, Long postId){
            List<PostResponseDto> postResponseDtoList = new ArrayList<>();

            Post post = postRepository.findById(postId).orElse(null);
            if (post == null) {
                throw new NullPointerException("존재하지 않는 게시글 입니다..");
            }

            List<LocationTags> locationTag = locationTagsRepository.findAllByPost_Id(post.getId());
            List<String> locationTags = new ArrayList<>();
            for (LocationTags a : locationTag) {
                locationTags.add(a.getLocationTags());
            }

            List<Tags> tag = tagRepository.findAllByPost_Id(post.getId());
            List<String> tags = new ArrayList<>();
            for (Tags b : tag) {
                tags.add(b.getTag());
            }

            List<MultipartFiles> multipartFile = imgRepository.findAllByPost_Id(post.getId());
            List<String> multipartFiles = new ArrayList<>();
            for (MultipartFiles c : multipartFile) {
                multipartFiles.add(c.getMultipartFiles());
            }
            Member member = new Member();

            if (userDetails != null) {
                member = ((PrincipalDetails) userDetails).getMember();
            }

            Boolean isMine = false;
            Boolean isHeartMine = false;

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
                    .locationTags(locationTags)
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
        public ResponseDto<?> getAllMyPost (UserDetails userDetails) {

            if (userDetails != null) {
                Member member = ((PrincipalDetails) userDetails).getMember();
                if (member != null) {
                    List<Post> postList = postRepository.findAllByMember_IdOrderByCreatedAtDesc(member.getId());
//                    System.out.println("member.getId---------------" + member.getId());
//                    System.out.println("postList.size()------------" + postList.size());
//                    System.out.println("postList-------------------" + postList);
//                    for (Post a : postList) {
//                        System.out.println(a.getContent());
//                    }
                    List<PostResponseDto> postResponseDtos = convertToBasicResponseDto2(postList, userDetails);
                    return ResponseDto.success(postResponseDtos);
                }
            }

           // PageRequest pageRequest = PageRequest.of(page, size);

           // Page<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageRequest);

            return ResponseDto.fail(404,"회원 정보가 없음");
        }


        // Todo :: 게시글 삭제
        @Transactional
        public ResponseDto<?> deletePost (Long postId, UserDetails userDetails){
            Post post = postRepository.findById(postId).orElse(null);
            Member member = new Member();
            if (userDetails != null) {
                member = ((PrincipalDetails) userDetails).getMember();
            }
            if (post == null) {
                throw new NullPointerException("존재하지 않는 게시글 입니다.");
            }
            if (!member.getProviderId().equals(post.getMember().getProviderId())) {
                throw new NullPointerException("게시글 주인이 아닙니다.");
            } else {
                postRepository.delete(post);
                return ResponseDto.success("게시글 삭제 성공");
            }
        }
        // Todo :: pagenation 처리
        private Page<PostResponseDto> convertToBasicResponseDto (Page < Post > postList, UserDetails userDetails){

            Member member = new Member();

            if (userDetails != null) {
                member = ((PrincipalDetails) userDetails).getMember();
            }

            Boolean isMine = false;
            Boolean isHeartMine = false;
            Boolean isImages = false;

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
                if (multipartFile.size() > 1) {
                    isImages = true;
                }
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
        // Mypage test
    private List<PostResponseDto> convertToBasicResponseDto2 (List <Post> postList, UserDetails userDetails){

        Member member = new Member();

        if (userDetails != null) {
            member = ((PrincipalDetails) userDetails).getMember();
        }

        Boolean isMine = false;
        Boolean isHeartMine = false;
        Boolean isImages = false;

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
            if (multipartFile.size() > 1) {
                isImages = true;
            }
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
        public Member validateMember (HttpServletRequest request){
            String accessToken = resolveToken(request.getHeader("Authorization"));

            if (!jwtTokenProvider.validationToken(accessToken)) {
                return null;
            }
            return jwtTokenProvider.getMemberFromAuthentication();
        }

        // Todo :: Authorization 에서 받아온 accessToken bearer 제거
        private String resolveToken (String accessToken){
            if (accessToken.startsWith("Bearer ")) {
                return accessToken.substring(7);
            }
            throw new RuntimeException("NOT VALID ACCESS TOKEN");
        }
    }




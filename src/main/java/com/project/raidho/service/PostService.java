package com.project.raidho.service;

import com.project.raidho.domain.MultipartFiles;
import com.project.raidho.domain.Post;
import com.project.raidho.domain.Tags;
import com.project.raidho.domain.Timestamped;
import com.project.raidho.dto.request.PostRequestDto;
import com.project.raidho.dto.resposnse.PostResponseDto;
import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.jwt.JwtTokenProvider;
import com.project.raidho.repository.ImgRepository;
import com.project.raidho.repository.PostRepository;
import com.project.raidho.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService extends Timestamped {

    private final JwtTokenProvider jwtTokenProvider;
    private final PostRepository postRepository;

    private final ImgRepository imgRepository;

    private final TagRepository tagRepository;


//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;


    //게시물 업로드
    @Transactional
    public ResponseDto<?> createPost(PostRequestDto postRequestDto, List<String> imgPaths) {
//        if (null == request.getHeader("Refresh-Token")){
//            return ResponseDto.fail("401","No right to create new post, Please login.");
//        }
//        if (null == request.getHeader("Authorization")){
//            return ResponseDto.fail("401","No right to create new post, Please login.");
//        }
//        if (null == member) {
//            return ResponseDto.fail("400","Fail to create new post.(Token값이 유효하지 않습니다.)");
//        }
        List<String> tags = postRequestDto.getTags();
        Post post = Post.builder()
                .content(postRequestDto.getContent())
                .tags(postRequestDto.getTags())
                .multipartFiles(postRequestDto.getMultipartFile())
//                .member(member
                .build();
        postRepository.save(post);


//        MembersDto membersDto = MembersDto.builder()
//                .memberName(post.getMember().getMemberName())
//                .memberImage(post.getMember().getMemberImage())
//                .build();
        List<String> imgList = new ArrayList<>();
        for (String imgUrl : imgPaths) {
            MultipartFiles multipartFiles = new MultipartFiles(imgUrl, post);
            imgRepository.save(multipartFiles);
            imgList.add(multipartFiles.getMultipartFiles());

            for (String tag : tags)
                tagRepository.save(
                        Tags.builder()
                                .tag(tag)
                                .post(post)
                                .build()
                );


            return ResponseDto.success(
                    PostResponseDto.builder()
                            .id(post.getId())
                            .content(post.getContent())
                            .tags(post.getTags())
//                        .author(membersDto)
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
                            .build()
            );



        }
        return ResponseDto.success("400");
    }
}




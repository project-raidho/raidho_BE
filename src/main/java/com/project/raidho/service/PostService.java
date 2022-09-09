package com.project.raidho.service;

import com.project.raidho.domain.Post;
import com.project.raidho.domain.Timestamped;
import com.project.raidho.dto.request.ContentRequestDto;
import com.project.raidho.dto.resposnse.PostResponseDto;
import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.jwt.JwtTokenProvider;
import com.project.raidho.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PostService extends Timestamped {

    private final JwtTokenProvider jwtTokenProvider;
    private final PostRepository postRepository;

//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;


    //게시물 업로드
    @Transactional
    public ResponseDto<?> createPost(ContentRequestDto contentRequestDto) {
//        if (null == request.getHeader("Refresh-Token")){
//            return ResponseDto.fail("401","No right to create new post, Please login.");
//        }
//        if (null == request.getHeader("Authorization")){
//            return ResponseDto.fail("401","No right to create new post, Please login.");
//        }
//        if (null == member) {
//            return ResponseDto.fail("400","Fail to create new post.(Token값이 유효하지 않습니다.)");
//        }
        Post post = Post.builder()
                .content(contentRequestDto.getContent())
//                .member(member)

                .build();
        postRepository.save(post);

//        MembersDto membersDto = MembersDto.builder()
//                .memberName(post.getMember().getMemberName())
//                .memberImage(post.getMember().getMemberImage())
//                .build();

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


}

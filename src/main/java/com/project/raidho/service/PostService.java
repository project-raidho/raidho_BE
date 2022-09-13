package com.project.raidho.service;

import com.project.raidho.domain.*;
import com.project.raidho.dto.request.PostRequestDto;
import com.project.raidho.dto.resposnse.PostResponseDto;
import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.repository.ImgRepository;
import com.project.raidho.repository.LocationTagsRepository;
import com.project.raidho.repository.PostRepository;
import com.project.raidho.repository.TagRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Builder
@RequiredArgsConstructor
public class PostService extends Timestamped {
    private final PostRepository postRepository;
    private final ImgRepository imgRepository;
    private final TagRepository tagRepository;
    private final S3Service s3Service;
    private final LocationTagsRepository locationTagsRepository;

    //게시물 업로드
    @Transactional
    public ResponseDto<?> createPost(PostRequestDto postRequestDto) throws IOException {
//        if (null == request.getHeader("Refresh-Token")){
//            return ResponseDto.fail("401","No right to create new post, Please login.");
//        }
//        if (null == request.getHeader("Authorization")){
//            return ResponseDto.fail("401","No right to create new post, Please login.");
//        }
//        if (null == member) {
//            return ResponseDto.fail("400","Fail to create new post.(Token값이 유효하지 않습니다.)");
//        }
        Post post = postRepository.save(
                Post.builder()
                        .content(postRequestDto.getContent())
                        .build()
        );
//        MembersDto membersDto = MembersDto.builder()
//                .memberName(post.getMember().getMemberName())
//                .memberImage(post.getMember().getMemberImage())
//                .build();

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


//    @Transactional(readOnly = true)
//    public ResponseDto<?> getAllPost(int page, int size){
//        PageRequest pageRequest=PageRequest.of(page,size);
//        Page<Post> postList = postRepository.findAll(pageRequest);
//        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

//        for (Post post : postList){
////            MembersResponseDto membersResponseDto = MembersResponseDto.builder()
////                    .memberName(post.getMember().getMemberName())
////                    .memberImage(post.getMember().getMemberImage())
////                    .build();
//            List<LocationTags> locationTag = locationTagsRepository.findAllByPost_Id(post.getId());
//            List<String> locationTags = new ArrayList<>();
//            for(LocationTags a : locationTag){
//                locationTags.add(a.getLocationTags());
//            }
//            List<Tags> tag = tagRepository.findAllByPost_Id(post.getId());
//            List<String> tags = new ArrayList<>();
//            for(Tags b : tag){
//                tags.add(b.getTag());
//            }
//
//
//            PostResponseDto postResponseDto=PostResponseDto.builder()
//                    .content(post.getContent())
//                    .id(post.getId())
////                    .membersResponseDto(membersResponseDto)
//                    .locationTags(locationTags)
//                    .tags(tags)
//                    .createdAt(post.getCreatedAt())
//                    .modifiedAt(post.getModifiedAt())
//                    .multipartFiles(post.getMultipartFiles())
//                    .build();
//            postResponseDtoList.add(postResponseDto);
//        }
       // return ResponseDto.success(postResponseDtoList);
//        return ResponseDto.success(null);
//    }
//
}





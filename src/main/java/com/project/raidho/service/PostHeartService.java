package com.project.raidho.service;



import com.project.raidho.domain.Post;
import com.project.raidho.domain.PostHeart;
import com.project.raidho.domain.member.Member;
import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.jwt.JwtTokenProvider;
import com.project.raidho.repository.PostHeartRepository;
import com.project.raidho.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PostHeartService {

    private final PostRepository postRepository;
    private final PostHeartRepository postHeartRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @Transactional
    public ResponseDto<?> createPostHeart(Long postId, HttpServletRequest request) {

        Member member = validateMember(request);


        Post post = postRepository.findByIdAndActivateIsTrue(postId).orElse(null);

        //게시글 좋아요한 사람 0

        Optional<PostHeart> postHeartOptional = postHeartRepository.findByPostAndMember(post, member);
        if (postHeartOptional.isPresent()) {
            return ResponseDto.fail("000","이미 좋아요를 누르셨습니다.");
        }

        if (!postHeartOptional.isPresent()) {
//            post.update(post.getHeartCount());
            PostHeart postHeart = PostHeart.builder()
                    .postHeart(1)
                    .member(member)
                    .post(post)
                    .build();
            postHeartRepository.save(postHeart);
            List<PostHeart> postHearts = postHeartRepository.findByPost(post);
            post.update(postHearts);

        }
        return ResponseDto.success("좋아요");
    }

    @Transactional
    public ResponseDto<?> deletePostHeart(Long postId, HttpServletRequest request) {

        Member member = validateMember(request);

        Post post = postRepository.findByIdAndActivateIsTrue(postId).orElse(null);

        //게시글 좋아요한 사람 0

        Optional<PostHeart> postHeartOptional = postHeartRepository.findByPostAndMember(post, member);
        if (!postHeartOptional.isPresent()) {
            return ResponseDto.fail("000","좋아요를 누르지 않으셨습니다.");
        }
        postHeartRepository.delete(postHeartOptional.get());
        List<PostHeart> postHearts = postHeartRepository.findByPost(post);
        post.update(postHearts);
        return ResponseDto.success("좋아요 취소");
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
//        if (!jwtTokenProvider.validateToken(request.getHeader("RefreshToken"))) {
//            return null;
//        }
//        return jwtTokenProvider.getMemberFromAuthentication();
        return null;
    }
}

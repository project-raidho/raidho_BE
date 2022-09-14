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
import java.io.IOException;
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
        if (member == null) {
            throw new NullPointerException("회원만 사용 가능합니다.");
        }

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NullPointerException("존재하지 않는 게시글 입니다.");
        }
        Optional<PostHeart> postHeartOptional = postHeartRepository.findByPostAndMember(post, member);
        if (postHeartOptional.isPresent()) {
            throw new NullPointerException("이미 좋아요를 눌렀습니다.");
        }
        if (!postHeartOptional.isPresent()) {
            PostHeart postHeart = PostHeart.builder()
                    .postHeart(1)
                    .member(member)
                    .post(post)
                    .build();
            postHeartRepository.save(postHeart);
            List<PostHeart> postHearts = postHeartRepository.findByPost(post);
            post.update(postHearts);


        }
        return ResponseDto.success("좋아요 증가");
    }

    @Transactional
    public ResponseDto<?> deletePostHeart(Long postId, HttpServletRequest request) {
        Member member = validateMember(request);
        if (member == null) {
            throw new NullPointerException("회원만 사용 가능합니다.");
        }

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new NullPointerException("존재하지 않는 게시글 입니다..");
        }

        Optional<PostHeart> postHeartOptional = postHeartRepository.findByPostAndMember(post, member);
        if (!postHeartOptional.isPresent()) {
            throw new NullPointerException("좋아요를 누르지않았습니다.");
        }
        postHeartRepository.delete(postHeartOptional.get());
        List<PostHeart> postHearts = postHeartRepository.findByPost(post);
        post.update(postHearts);
        return ResponseDto.success("좋아요 감소");
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        String accessToken = resolveToken(request.getHeader("Authorization"));

        if (!jwtTokenProvider.validationToken(accessToken)) {
            return null;
        }
        return jwtTokenProvider.getMemberFromAuthentication();
    }

    private String resolveToken(String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }
        throw new RuntimeException("NOT VALID ACCESS TOKEN");
    }
}

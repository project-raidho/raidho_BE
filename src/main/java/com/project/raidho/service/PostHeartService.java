package com.project.raidho.service;

import com.project.raidho.domain.post.Post;
import com.project.raidho.domain.postHeart.PostHeart;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.ResponseDto;
import com.project.raidho.exception.ErrorCode;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.jwt.JwtTokenProvider;
import com.project.raidho.repository.PostHeartRepository;
import com.project.raidho.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class PostHeartService {

    private final PostRepository postRepository;

    private final PostHeartRepository postHeartRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ResponseEntity<?> createPostHeart(Long postId, HttpServletRequest request) throws RaidhoException {
        Member member = validateMember(request);
        if (member == null) {
            log.error(ErrorCode.UNAUTHORIZATION_MEMBER.getErrorMessage());
            throw new RaidhoException(ErrorCode.UNAUTHORIZATION_MEMBER);
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_POST));
        Optional<PostHeart> postHeartOptional = postHeartRepository.findByPostAndMember(post, member);
        if (!postHeartOptional.isPresent()) {
            PostHeart postHeart = PostHeart.builder()
                    .postHeart(1)
                    .member(member)
                    .post(post)
                    .build();
            postHeartRepository.save(postHeart);
            List<PostHeart> postHearts = postHeartRepository.findByPost(post);
            post.update(postHearts);
            return ResponseEntity.ok().body(ResponseDto.success("좋아요!!!"));

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.fail(400, "이미 좋아요를 체크하셨습니다."));
    }

    @Transactional
    public ResponseEntity<?> deletePostHeart(Long postId, HttpServletRequest request) throws RaidhoException {
        Member member = validateMember(request);
        if (member == null) {
            throw new RaidhoException(ErrorCode.UNAUTHORIZATION_MEMBER);
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_POST));
        Optional<PostHeart> postHeartOptional = postHeartRepository.findByPostAndMember(post, member);
        if (!postHeartOptional.isPresent()) {
            throw new RaidhoException(ErrorCode.DIDNT_CHECK_LIKE);
        }
        postHeartRepository.delete(postHeartOptional.get());
        List<PostHeart> postHearts = postHeartRepository.findByPost(post);
        post.update(postHearts);
        return ResponseEntity.ok().body(ResponseDto.success("좋아요를 취소하셨습니다."));
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

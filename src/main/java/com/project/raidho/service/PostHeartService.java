package com.project.raidho.service;

import com.project.raidho.domain.Post;
import com.project.raidho.domain.PostHeart;
import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.repository.PostHeartRepository;
import com.project.raidho.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostHeartService {

    private final PostRepository postRepository;
    private final PostHeartRepository postHeartRepository;

    @Transactional
    public ResponseDto<?> createPostHeart(Long postId) {

        Post post = postRepository.findById(postId).orElse(null);

            PostHeart postHeart = PostHeart.builder()
                    .postHeart(1)
                    .post(post)
                    .build();
            postHeartRepository.save(postHeart);
            List<PostHeart> postHearts = postHeartRepository.findByPost(post);
            post.update(postHearts);

        return ResponseDto.success("좋아요");
    }

    @Transactional
    public ResponseDto<?> deletePostHeart(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);

        List<PostHeart> postHearts = postHeartRepository.findByPost(post);
        post.update(postHearts);
        return ResponseDto.success("좋아요 취소");
    }

}

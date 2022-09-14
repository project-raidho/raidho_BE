package com.project.raidho.repository;

import com.project.raidho.domain.Post;
import com.project.raidho.domain.PostHeart;
import com.project.raidho.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHeartRepository extends JpaRepository<PostHeart, Long> {
    List<PostHeart> findByPost(Post post);
    Optional<PostHeart> findByPostAndMember(Post post, Member member);
}
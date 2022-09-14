package com.project.raidho.repository;

import com.project.raidho.domain.Post;
import com.project.raidho.domain.PostHeart;
import com.project.raidho.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostHeartRepository extends JpaRepository<PostHeart, Long> {
    List<PostHeart> findByPost(Post post);
    Optional<PostHeart> findByPostAndMember(Post post, Member member);

    @Query("SELECT COUNT(u) FROM PostHeart u WHERE u.post = :post")
    int getCountOfPostHeart(@Param(value = "post") Post post);
}
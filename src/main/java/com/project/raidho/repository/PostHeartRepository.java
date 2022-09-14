package com.project.raidho.repository;

import com.project.raidho.domain.Post;
import com.project.raidho.domain.PostHeart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostHeartRepository extends JpaRepository<PostHeart, Long> {
    List<PostHeart> findByPost(Post post);
}
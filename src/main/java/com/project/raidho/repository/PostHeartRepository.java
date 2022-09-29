package com.project.raidho.repository;

import com.project.raidho.domain.post.Post;
import com.project.raidho.domain.postHeart.PostHeart;
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

    @Query("SELECT COUNT(u) FROM PostHeart u WHERE u.post = :post and u.member = :member")
    int getCountOfPostAndMemberPostHeart(@Param(value = "post") Post post, @Param(value = "member") Member member);

    @Query("SELECT u.post FROM PostHeart u WHERE u.member = :member")
    List<Post> getMyHeartPost(@Param(value = "member") Member member);

}
package com.project.raidho.repository;

import com.project.raidho.domain.chat.ChatMessage;
import com.project.raidho.domain.comment.Comment;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.Post;
import com.project.raidho.domain.postHeart.PostHeart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

//    Page<Comment> findAllByOrderByCreatedAtDesc(PageRequest pageRequest);

    List<Comment> findByPost(Post post);

    Page<Comment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

    @Query("SELECT COUNT(u) FROM Comment u WHERE u.post = :post")
    int getCountOfComment(@Param(value = "post") Post post);

    @Query("SELECT DISTINCT c.post FROM Comment c WHERE c.member = :member")
    List<Post> getMyCommentedPost(@Param(value = "member") Member member);

}

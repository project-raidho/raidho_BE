package com.project.raidho.repository;

import com.project.raidho.domain.chat.ChatMessage;
import com.project.raidho.domain.comment.Comment;
import com.project.raidho.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

//    Page<Comment> findAllByOrderByCreatedAtDesc(PageRequest pageRequest);

    Page<Comment> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);
}

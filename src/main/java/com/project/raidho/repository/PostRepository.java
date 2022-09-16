package com.project.raidho.repository;

import com.project.raidho.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findAllByOrderByCreatedAtDesc (PageRequest pageRequest);
    Page<Post> findAllByOrderByHeartCountDesc (PageRequest pageRequest);
    Optional<Post> findById(Long id);
    List<Post> findAllByMember_IdOrderByCreatedAtDesc(Long id);
}

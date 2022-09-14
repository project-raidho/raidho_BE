package com.project.raidho.repository;

import com.project.raidho.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface PostRepository extends JpaRepository<Post,Long> {

    Page<Post> findAllByOrderByCreatedAtDesc (PageRequest pageRequest);
//    Optional<Post> findByIdAndActivateIsTrue(Long id);
//    List<Post> findAll (PageRequest pageRequest);

    Page<Post> findAllByOrderByHeartCountAsc (PageRequest pageRequest);

    Optional<Post> findById(Long id);
}

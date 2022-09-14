package com.project.raidho.repository;

import com.project.raidho.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface PostRepository extends JpaRepository<Post,Long> {

//    List<Post> findAll (PageRequest pageRequest);
    Optional<Post> findById(Long id);
}

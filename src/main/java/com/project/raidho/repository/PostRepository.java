package com.project.raidho.repository;

import com.project.raidho.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post>findAllByOrderByModifiedAtDesc();
}

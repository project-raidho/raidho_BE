package com.project.raidho.repository;

import com.project.raidho.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findAllByOrderByCreatedAtDesc (PageRequest pageRequest);
    Page<Post> findAllByOrderByHeartCountDesc (PageRequest pageRequest);
    Optional<Post> findById(Long id);

    @Query("SELECT T,L FROM Tags T INNER JOIN LocationTags L ON T.tag=L.locationTags WHERE T.tag = :tag")
    Page<Post> SearchTag(@Param(value = "tag") String tag);
}

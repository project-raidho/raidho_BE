package com.project.raidho.repository;

import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.Post;
import com.project.raidho.domain.tags.Tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tags, Long> {

    List<Tags> findAllByPost_Id(Long id);

    void deleteAllByPost_Id(Long id);

    @Query("SELECT DISTINCT t.post FROM Tags t WHERE t.tag like %:tag%")
    Page<Post> SearchTag(@Param(value = "tag") String tag, PageRequest pageRequest);

    @Query("SELECT DISTINCT t.post FROM Tags t WHERE t.tag like %:tag% and t.post.member <> :member ")
    Page<Post> distinctMyPostSearchTag(@Param(value = "tag") String tag, @Param(value = "member") Member member, PageRequest pageRequest);

}

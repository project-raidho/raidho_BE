package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.tags.MeetingTags;
import com.project.raidho.domain.tags.Tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeetingTagRepository extends JpaRepository<MeetingTags,Long> {
//    List<Tags> findAllByPost_Id(Long id);
//
//    void deleteAllByPost_Id(Long id);
//
//    @Query("SELECT DISTINCT t.meetingPost FROM MeetingTags t WHERE t.meetingTag like %:tag%")
//    Page<MeetingPost> SearchTag(@Param(value = "tag") String tag, PageRequest pageRequest);
}

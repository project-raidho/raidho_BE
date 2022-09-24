package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.post.Post;
import com.project.raidho.domain.tags.MeetingTags;
import com.project.raidho.domain.tags.Tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeetingTagRepository extends JpaRepository<MeetingTags,Long> {
//    List<MeetingTags> findAllByMeetingPost_Id(Long id);
//
//    void deleteAllByPost_Id(Long id);
//
//    @Query("SELECT DISTINCT t.meetingPost FROM MeetingTags t WHERE t.meetingTag like %:tag%")
//    Page<MeetingPost> SearchTag(@Param(value = "tag") String tag, PageRequest pageRequest);

    List<MeetingTags> findByMeetingPost(MeetingPost meetingPost);

    @Query("SELECT DISTINCT t.meetingTag FROM MeetingTags t WHERE t.meetingTag like %:meetingTag%")
    Page<MeetingPost> SearchMeetingTag(@Param(value = "meetingTag") String meetingTag, PageRequest pageRequest);
}

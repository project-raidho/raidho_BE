package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.MeetingPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.servlet.tags.form.SelectTag;

import java.util.List;
import java.util.Optional;

public interface MeetingPostRepository extends JpaRepository<MeetingPost, Long> {

    Page<MeetingPost> findAllByOrderByCreatedAtDesc (PageRequest pageRequest);
    Page<MeetingPost> findAllByThemeCategory_IdOrderByCreatedAtDesc(Long id, PageRequest pageRequest);
    Optional<MeetingPost> findById(Long id);
    List<MeetingPost> findAllByMember_IdOrderByCreatedAtDesc(Long id);

    @Query("SELECT m FROM MeetingPost m WHERE m.roomCloseDate > :date ORDER BY m.createdAt DESC")
    Page<MeetingPost> getOpenMeetingRoom(@Param(value = "date") String date, PageRequest pageRequest);

    @Query("SELECT m FROM MeetingPost m WHERE m.roomCloseDate > :date and m.themeCategory.id = :id ORDER BY m.createdAt DESC ")
    Page<MeetingPost> getOpenMeetingRoomAndCategory(@Param(value = "date") String date, @Param(value = "countryName") Long id, PageRequest pageRequest);

}

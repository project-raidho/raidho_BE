package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.MeetingPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingPostRepository extends JpaRepository<MeetingPost, Long> {

    Page<MeetingPost> findAllByOrderByCreatedAtDesc (PageRequest pageRequest);
    Page<MeetingPost> findAllByThemeCategory_IdOrderByCreatedAtDesc(Long id, PageRequest pageRequest);
    Optional<MeetingPost> findById(Long id);
    List<MeetingPost> findAllByMember_IdOrderByCreatedAtDesc(Long id);

}

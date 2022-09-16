package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.MeetingPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<MeetingPost, Long> {
}

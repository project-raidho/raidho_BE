package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.MeetingPost;
import com.querydsl.core.QueryResults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryDslRepository {
    Page<MeetingPost> findGetOpenMeetingRoom(String date, Pageable pageable);
}

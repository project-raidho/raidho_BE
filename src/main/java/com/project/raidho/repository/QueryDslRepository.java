package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.MeetingPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface QueryDslRepository {
    Page<MeetingPost> findGetOpenMeetingRoom(String date, PageRequest pageRequest);
}

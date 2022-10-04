package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.post.Post;
import com.querydsl.core.QueryResults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryDslRepository {

    // Todo :: Post 구현할게 더 남음
    Page<Post> getAllPost(Pageable pageable);

    // Todo :: MeetingPost 구현할게 더 남음
    Page<MeetingPost> findGetOpenMeetingRoom(String date, Pageable pageable);
    Page<MeetingPost> findGetOpenMeetingRoomAndCategory(String date, Long id, Pageable pageable);
}

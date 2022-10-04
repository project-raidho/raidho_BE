package com.project.raidho.repository;

import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.meetingPostStar.MeetingPostStar;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MeetingPostStarRepository extends JpaRepository<MeetingPostStar, Long> {

    List<MeetingPostStar> findByMeetingPost(MeetingPost meetingPost);
    Optional<MeetingPostStar> findByMeetingPostAndMember(MeetingPost meetingPost, Member member);

    void deleteAllByMeetingPost(MeetingPost meetingPost);
//    @Query("SELECT COUNT(u) FROM MeetingPostStar u WHERE u.meetingPost = :meetingPost")
//    int getCountOfMeetingPostStar(@Param(value = "meetingPost") MeetingPost meetingPost);
    @Query("SELECT COUNT(u) FROM MeetingPostStar u WHERE u.meetingPost = :meetingPost and u.member = :member")
    int getCountOfMeetingPostAndMemberMeetingPostStar(@Param(value = "meetingPost") MeetingPost meetingPost, @Param(value = "member") Member member);

    @Query("SELECT u.meetingPost FROM MeetingPostStar u WHERE u.member = :member")
    List<MeetingPost> findMyStarMeetingPost(@Param(value = "member") Member member);
}
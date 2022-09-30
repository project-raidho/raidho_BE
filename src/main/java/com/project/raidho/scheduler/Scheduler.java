package com.project.raidho.scheduler;

import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.repository.ChatMessageRepository;
import com.project.raidho.repository.MeetingPostRepository;
import com.project.raidho.repository.MeetingPostStarRepository;
import com.project.raidho.repository.RoomDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class Scheduler {

    private final MeetingPostRepository meetingPostRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RoomDetailRepository roomDetailRepository;
    private final MeetingPostStarRepository meetingPostStarRepository;

    @Scheduled(cron = "* 1 * * * *") // 초(0-59) 분(0-59) 시(0-23) 일(1-31) 월(1-12) 요일(1-7)
    @Transactional
    public void deleteMeetingPost() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        List<MeetingPost> meetingPosts = meetingPostRepository.findAll();
        for (MeetingPost meetingPost : meetingPosts) {
            Date date = formatter.parse(meetingPost.getEndDate());
            Date deleteMeetingDate = new Date(date.getTime() + (1000 * 60 * 60 * 24));
            if (deleteMeetingDate.before(new Date())) {
                meetingPostStarRepository.deleteAllByMeetingPost(meetingPost);
                chatMessageRepository.deleteAllByRoomId(meetingPost.getId());
                roomDetailRepository.deleteByRoomMaster_RoomId(meetingPost.getId());
                meetingPostRepository.delete(meetingPost);
            }
        }
    }
}

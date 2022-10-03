package com.project.raidho.service;

import com.project.raidho.domain.MeetingStatusDto;
import com.project.raidho.domain.meetingPost.MeetingPost;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateCheckProvider {

    public MeetingStatusDto dateCheck(MeetingPost meetingPost, int memberCount) throws ParseException {
        MeetingStatusDto meetingStatusDto = new MeetingStatusDto();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(meetingPost.getRoomCloseDate());
        Date tomorrow = new Date(date.getTime() + (1000 * 60 * 60 * 24));
        if (tomorrow.after(new Date()) && (meetingPost.getPeople() > memberCount)) {
            meetingStatusDto.setMeetingStatus(1);
        } else if (tomorrow.after(new Date()) && memberCount >= meetingPost.getPeople()) {
            meetingStatusDto.setMeetingStatus(2);
        } else if (tomorrow.before(new Date())) {
            meetingStatusDto.setMeetingStatus(3);
        }
        return meetingStatusDto;
    }
}

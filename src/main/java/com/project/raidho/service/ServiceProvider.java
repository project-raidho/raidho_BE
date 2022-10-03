package com.project.raidho.service;

import com.project.raidho.domain.IsMineDto;
import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.member.Member;
import com.project.raidho.repository.MeetingPostStarRepository;
import com.project.raidho.repository.RoomDetailRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceProvider {

    private final RoomDetailRepository roomDetailRepository;
    private final MeetingPostStarRepository meetingPostStarRepository;

    public IsMineDto isMineCheck_Post(Member member) {
        return null;
    }

    public IsMineDto isMineCheck_MeetingPost(Member member, RoomMaster roomMaster, MeetingPost meetingPost) {
        IsMineDto isMineDto = new IsMineDto();
        if (member.getProviderId().equals(meetingPost.getMember().getProviderId())) {
            isMineDto.setMine(true);
        }
        RoomDetail roomDetails = roomDetailRepository.findByRoomMasterAndMember(roomMaster, member);
        if (roomDetails != null) {
            isMineDto.setAlreadyMine(true);
        }
        int isStarMineCh = meetingPostStarRepository.getCountOfMeetingPostAndMemberMeetingPostStar(meetingPost, member);
        if (isStarMineCh >= 1) {
            isMineDto.setStarMine(true);
        }
        return isMineDto;
    }
}

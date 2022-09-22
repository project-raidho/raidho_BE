package com.project.raidho.repository;

import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomDetailRepository extends JpaRepository<RoomDetail, Long> {
    RoomDetail findByRoomMasterAndMember(RoomMaster roomMaster, Member member);
    RoomDetail findByRoomMaster_RoomIdAndMember_Id(Long roomId, Long memberId);
}

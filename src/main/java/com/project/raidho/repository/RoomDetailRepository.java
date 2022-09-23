package com.project.raidho.repository;

import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomDetailRepository extends JpaRepository<RoomDetail, Long> {
    RoomDetail findByRoomMasterAndMember(RoomMaster roomMaster, Member member);
    RoomDetail findByRoomMaster_RoomIdAndMember_Id(Long roomId, Long memberId);

    @Query("SELECT COUNT(d) FROM RoomDetail d WHERE d.roomMaster =: roomMaster")
    int getCountJoinRoomMember(@Param(value = "roomMaster") RoomMaster roomMaster);
}

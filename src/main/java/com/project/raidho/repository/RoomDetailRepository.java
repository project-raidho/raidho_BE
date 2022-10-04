package com.project.raidho.repository;

import com.project.raidho.domain.chat.RoomDetail;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface RoomDetailRepository extends JpaRepository<RoomDetail, Long> {

    RoomDetail findByRoomMasterAndMember(RoomMaster roomMaster, Member member);

    Optional<RoomDetail> findByRoomMaster_RoomIdAndMember_Id(Long roomId, Long memberId);

    void deleteByRoomMaster_RoomId(Long roomId);

    void deleteByRoomMasterAndMember(RoomMaster roomMaster, Member member);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT(d) FROM RoomDetail d WHERE d.roomMaster = :roomMaster")
    int getCountJoinRoomMember(@Param(value = "roomMaster") RoomMaster roomMaster);

    @Query("SELECT d.member.id FROM RoomDetail d WHERE d.roomMaster = :roomMaster")
    List<Long> getAllMemberId(@Param(value = "roomMaster") RoomMaster roomMaster);

}

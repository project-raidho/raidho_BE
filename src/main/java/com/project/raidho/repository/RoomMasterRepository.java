package com.project.raidho.repository;

import com.project.raidho.domain.chat.ChatDto.RoomMasterResponseDto;
import com.project.raidho.domain.chat.RoomMaster;
import com.project.raidho.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomMasterRepository extends JpaRepository<RoomMaster, Long> {
    Optional<RoomMaster> findByRoomId(Long roomId);
    List<RoomMaster> findAllByRoomDetails_Member(Member member);
}

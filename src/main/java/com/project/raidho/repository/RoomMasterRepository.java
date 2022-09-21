package com.project.raidho.repository;

import com.project.raidho.domain.chat.RoomMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomMasterRepository extends JpaRepository<RoomMaster, Long> {
    Optional<RoomMaster> findByRoomId(Long roomId);
}

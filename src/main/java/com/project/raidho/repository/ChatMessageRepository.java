package com.project.raidho.repository;

import com.project.raidho.domain.chat.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    String COUNT_QUERY_STRING = "SELECT COUNT(*) FROM chat_message cm " +
            "LEFT OUTER JOIN room_detail rd ON cm.room_id = rd.room_master_id " +
            "WHERE " +
            "cm.id > :chatId";

    @Query(value = COUNT_QUERY_STRING, nativeQuery = true)
    Long countFromLastReadChat(@Param(value = "chatId") Long chatId);

    Page<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId, Pageable pageable);

    Optional<ChatMessage> findFirstByRoomIdOrderByCreatedAtDesc(Long roomId);

    void deleteAllByRoomId(Long roomId);

}

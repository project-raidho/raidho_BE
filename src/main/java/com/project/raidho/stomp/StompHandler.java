package com.project.raidho.stomp;

import com.project.raidho.jwt.JwtTokenProvider;
import com.project.raidho.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final RoomService roomService;

    // webSocket 을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // webSocket 연길시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            List<String> accessToken = accessor.getNativeHeader("Authorization");

            // 토큰이 없거나 토큰 검증에 실패한 경우
            if (accessToken == null || !jwtTokenProvider.validationToken(accessToken.get(0).substring(7))) {
                log.error("connection fail");
                throw new IllegalArgumentException("Invalid Token");
            }
            log.info("connection success");
        }
        return message;
    }

    @Override
    public void postSend(Message message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.UNSUBSCRIBE == accessor.getCommand()) {
            List<String> disconnectedMemberId = accessor.getNativeHeader("memberId");
            List<String> disconnectedRoomId = accessor.getNativeHeader("roomId");
            // 채팅방 unsubscribe 상태 감지
            if (disconnectedMemberId != null && disconnectedRoomId != null) {
                Long memberId = Long.parseLong(disconnectedMemberId.get(0));
                Long roomId = Long.parseLong(disconnectedRoomId.get(0));
                roomService.updateLastReadChat(roomId, memberId);
            }
        }
    }
}

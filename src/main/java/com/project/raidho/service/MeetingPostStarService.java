package com.project.raidho.service;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.meetingPost.MeetingPost;
import com.project.raidho.domain.meetingPostStar.MeetingPostStar;
import com.project.raidho.domain.member.Member;
import com.project.raidho.exception.ErrorCode;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.jwt.JwtTokenProvider;
import com.project.raidho.repository.MeetingPostRepository;
import com.project.raidho.repository.MeetingPostStarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingPostStarService {

    private final MeetingPostRepository meetingPostRepository;

    private final MeetingPostStarRepository meetingPostStarRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ResponseEntity<?> createMeetingPostStar(Long meetingPostId, HttpServletRequest request) throws RaidhoException {
        Member member = validateMember(request);
        if (member == null) {
            log.error(ErrorCode.DOESNT_EXIST_MEMBER.getErrorMessage());
            throw new RaidhoException(ErrorCode.UNAUTHORIZATION_MEMBER);
        }
        MeetingPost meetingPost = meetingPostRepository.findById(meetingPostId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_POST));
        Optional<MeetingPostStar> meetingPostStarOptional = meetingPostStarRepository.findByMeetingPostAndMember(meetingPost, member);
        if (!meetingPostStarOptional.isPresent()) {
            MeetingPostStar meetingPostStar = MeetingPostStar.builder()
                    .meetingPostStar(1)
                    .member(member)
                    .meetingPost(meetingPost)
                    .build();
            meetingPostStarRepository.save(meetingPostStar);
            log.info("{} 번 자랑글에 {} 님께서 좋아요 하셨습니다.", meetingPost.getId(), member.getMemberName());
            List<MeetingPostStar> meetingPostStars = meetingPostStarRepository.findByMeetingPost(meetingPost);
            meetingPost.update(meetingPostStars);
            return ResponseEntity.ok().body(ResponseDto.success("별찍힘"));

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.fail(400, "이미 별을 체크하셨습니다."));
    }

    @Transactional
    public ResponseEntity<?> deleteMeetingPostStar(Long meetingPostId, HttpServletRequest request) throws RaidhoException {
        Member member = validateMember(request);
        if (member == null) {
            log.error(ErrorCode.DOESNT_EXIST_MEMBER.getErrorMessage());
            throw new RaidhoException(ErrorCode.UNAUTHORIZATION_MEMBER);
        }
        MeetingPost meetingPost = meetingPostRepository.findById(meetingPostId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_POST));
        Optional<MeetingPostStar> meetingPostStarOptional = meetingPostStarRepository.findByMeetingPostAndMember(meetingPost, member);
        if (!meetingPostStarOptional.isPresent()) {
            log.error(ErrorCode.DIDNT_CHECK_LIKE.getErrorMessage());
            throw new RaidhoException(ErrorCode.DIDNT_CHECK_LIKE);
        }
        meetingPostStarRepository.delete(meetingPostStarOptional.get());
        log.info("{} 번 자랑글에 {} 님께서 좋아요를 취소하셨습니다.", meetingPost.getId(), member.getMemberName());
        List<MeetingPostStar> postHearts = meetingPostStarRepository.findByMeetingPost(meetingPost);
        meetingPost.update(postHearts);
        return ResponseEntity.ok().body(ResponseDto.success("별을 취소하셨습니다."));
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        String accessToken = resolveToken(request.getHeader("Authorization"));

        if (!jwtTokenProvider.validationToken(accessToken)) {
            return null;
        }
        return jwtTokenProvider.getMemberFromAuthentication();
    }

    private String resolveToken(String accessToken) {
        if (accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }
        throw new RuntimeException("NOT VALID ACCESS TOKEN");
    }
}
package com.project.raidho.service;

import com.project.raidho.domain.chat.ChatMessage;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.member.dto.MemberUpdateDto;
import com.project.raidho.exception.ErrorCode;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.repository.ChatMessageRepository;
import com.project.raidho.repository.MemberRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@Builder
@RequiredArgsConstructor
public class EditMemberService {

    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final S3Service s3Service;

    @Transactional
    public ResponseEntity<?> editMyPage(Long memberId, MemberUpdateDto memberDto) throws RaidhoException, IOException {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_MEMBER));
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByMember(member);
        MultipartFile multipartFile = memberDto.getMemberImage();
        if (multipartFile != null) {
            String updateImage = s3Service.upload(multipartFile);
            member.update(memberDto, updateImage);
            if (chatMessageList != null) {
                for (ChatMessage chatMessage : chatMessageList) {
                    chatMessage.MessageMemberUpdate(member);
                }
            }
        }
        if (multipartFile == null) {
            String updateImage = member.getMemberImage();
            member.update(memberDto, updateImage);
            if (chatMessageList != null) {
                for (ChatMessage chatMessage : chatMessageList) {
                    chatMessage.MessageMemberUpdate(member);
                }
            }
        }
        log.info("{} 님의 마이페이미지가 변경되었습니다.", member.getMemberName());
        return ResponseEntity.ok().body(member.getMemberImage());
    }

}

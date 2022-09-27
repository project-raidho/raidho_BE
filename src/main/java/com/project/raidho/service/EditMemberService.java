package com.project.raidho.service;

import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.member.dto.MemberUpdateDto;
import com.project.raidho.exception.ErrorCode;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.repository.MemberRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Slf4j
@Service
@Builder
@RequiredArgsConstructor
public class EditMemberService {

    private final MemberRepository memberRepository;

    private final S3Service s3Service;

    @Transactional
    public ResponseEntity<?> editMyPage(Long memberId, MemberUpdateDto memberDto) throws RaidhoException, IOException {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_MEMBER));
        MultipartFile multipartFile = memberDto.getMemberImage();
        if (multipartFile != null) {
            String updateImage = s3Service.upload(multipartFile);
            member.update(memberDto, updateImage);
        }
        if (multipartFile == null) {
            String updateImage = member.getMemberImage();
            member.update(memberDto, updateImage);
        }
        log.info("{} 님의 마이페이미지가 변경되었습니다.", member.getMemberName());
        return ResponseEntity.ok().body("마이페이지가 정상적으로 변경되었습니다.");
    }

}

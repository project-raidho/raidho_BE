package com.project.raidho.service;

import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.member.dto.MemberDto;
import com.project.raidho.domain.member.dto.MemberUpdateDto;
import com.project.raidho.domain.post.Post;
import com.project.raidho.exception.ErrorCode;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.repository.MemberRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Builder
@RequiredArgsConstructor
public class EditMemberService {

    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    @Transactional
    public void editMyPage(Long memberId, MemberUpdateDto memberDto) throws RaidhoException, IOException {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_MEMBER));
        MultipartFile multipartFile = (MultipartFile) memberDto.getMemberImage();
        String updateImage = s3Service.upload(multipartFile);
        member.update(memberDto, updateImage);
    }
}

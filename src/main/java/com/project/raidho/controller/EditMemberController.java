package com.project.raidho.controller;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.domain.member.dto.MemberDto;
import com.project.raidho.domain.member.dto.MemberUpdateDto;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.service.EditMemberService;
import com.project.raidho.service.S3Service;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class EditMemberController {
    private final EditMemberService editMemberService;

    @PostMapping("/{memberId}")
    public ResponseDto<?> editMyPage (@PathVariable("memberId") Long memberId, @ModelAttribute MemberUpdateDto memberDto) throws RaidhoException, IOException {
        editMemberService.editMyPage(memberId, memberDto);
        return ResponseDto.success("프로필 수정 성공!!!");
    }
}

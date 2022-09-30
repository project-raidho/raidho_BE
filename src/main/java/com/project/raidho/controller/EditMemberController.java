package com.project.raidho.controller;

import com.project.raidho.domain.member.dto.MemberUpdateDto;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.service.EditMemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class EditMemberController {
    private final EditMemberService editMemberService;
    // 마이페이지 수정
    @PutMapping("/{memberId}")
    public ResponseEntity<?> editMyPage (@PathVariable("memberId") Long memberId, @ModelAttribute MemberUpdateDto memberDto) throws RaidhoException, IOException {
        return editMemberService.editMyPage(memberId, memberDto);
    }
}

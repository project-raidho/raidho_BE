package com.project.raidho.service;

import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.member.dto.MemberDto;
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

@Service
@Builder
@RequiredArgsConstructor
public class EditMemberService {

    private final MemberRepository memberRepository;
    @Transactional
    public void editMyPage(Long memberId) throws RaidhoException {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RaidhoException(ErrorCode.DOESNT_EXIST_POST));

        member.update(
                member.getMemberImage(), member.getMemberIntro()
        );
    }
}

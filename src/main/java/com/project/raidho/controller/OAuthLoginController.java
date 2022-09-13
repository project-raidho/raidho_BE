package com.project.raidho.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.raidho.domain.MembersResponseDto;
import com.project.raidho.domain.member.dto.OauthLoginResponseDto;

import com.project.raidho.service.KakaoMemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/login/oauth2/code")
public class OAuthLoginController {

    private final KakaoMemberService kakaoMemberService;
//    private final NaverMemberService naverMemberService;
//    private final FacebookMemberService facebookMemberService;


    // 카카오 로그인
    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoLogin (@RequestParam String code, HttpServletResponse response) {
        try {
            OauthLoginResponseDto oauthLoginResponseDto = kakaoMemberService.kakaoLogin(code, response);
            MembersResponseDto membersResponseDto = new MembersResponseDto(oauthLoginResponseDto);
            return ResponseEntity.ok().body(membersResponseDto);
            //return new ResponseEntity<>("카카오 로그인 성공", HttpStatus.OK);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("카카오 로그인 실패");
        }
    }

//    @GetMapping("/naver")
//    public ResponseEntity<?> naverLogin (@RequestParam String code, @RequestParam String state, HttpServletResponse response) {
//        try {
//            OauthLoginResponseDto oauthLoginResponseDto = naverMemberService.naverLogin(code,state,response);
//            MemberDto memberDto = new MemberDto(oauthLoginResponseDto);
//            return ResponseEntity.ok().headers(oauthLoginResponseDto.getHttpHeaders()).
//                    body(memberDto);
//        } catch (JsonProcessingException e) {
//            log.error(e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("네이버 로그인 실패");
//        }
//    }

//    @GetMapping("/facebook")
//    public ResponseEntity<?> facebookLogin (@RequestParam String code, HttpServletResponse response) {
//        try {
//            OauthLoginResponseDto oauthLoginResponseDto = facebookMemberService.facebookLogin(code,response);
//            MemberDto memberDto = new MemberDto(oauthLoginResponseDto);
//            return ResponseEntity.ok().headers(oauthLoginResponseDto.getHttpHeaders()).
//                    body(memberDto);
//
//        } catch (JsonProcessingException e) {
//            log.error(e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("페이스북 로그인 실패");
//        }
//    }
}

package com.project.raidho.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.raidho.service.KakaoMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/login/oauth2/code")
public class OAuthLoginController {

    private final KakaoMemberService kakaoMemberService;

    // 카카오 로그인
    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoLogin (@RequestParam String code, HttpServletResponse response) {
        try {
            kakaoMemberService.kakaoLogin(code, response);
            return new ResponseEntity<>("카카오 로그인 성공", HttpStatus.OK);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("카카오 로그인 실패");
        }
    }


}

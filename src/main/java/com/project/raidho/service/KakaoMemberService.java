package com.project.raidho.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.raidho.domain.oauthMemberInfo.OauthMemberInfoImpl;
import com.project.raidho.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class KakaoMemberService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    String kakaoClientSecret;
    @Value(" ${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    String kakaoRedirect;

    private final MemberRepository memberRepository;

    // 카카오 로그인
    @Transactional
    public void kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. auth code 로 (Kakao -> Resource Server 접속이 가능한 ) accessToken 요청
        String KakaoResourceToken = getResourceToken(code); // 받은 인가코드를 통해 토큰을 받아옴
        OauthMemberInfoImpl kakaoMemberInfo = getKakaoMemberInfo(KakaoResourceToken);
    }

    private String getResourceToken(String code) throws JsonProcessingException {
        // Kakao Server 에서 받을 Http 를 만들기 위해 Http Header, body 생성
        // header 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("client_secret", kakaoClientSecret);
        body.add("redirect_uri", "http://localhost:8080/login/oauth2/code/kakao");
        // body.add("redirect_uri", kakaoRedirect);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoResourceTokenRequest =
                new HttpEntity<>(body, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoResourceTokenRequest,
                String.class
        );

        // Kakao Server accessToken response (JSON)
        String ResponseResourceToken = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper(); // json 형태의 객체 생성 (읽는 공간 생성)
        JsonNode jsonNode = objectMapper.readTree(ResponseResourceToken);
        return jsonNode.get("access_token").asText();
    }


    // 발급받은 토큰으로 Resource Server 에서 토큰을 검증해 유효한 토큰이면 User Info 를 response 해줌
    private OauthMemberInfoImpl getKakaoMemberInfo(String KakaoResourceToken) throws JsonProcessingException{
        //Resource Server => new
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer "+ KakaoResourceToken);
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoResourceInfoRequest = new HttpEntity<>(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoResourceInfoRequest,
                String.class
        );

        String ResponseResourceInfo = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(ResponseResourceInfo);
        System.out.println(jsonNode);

        String providerId = jsonNode.get("id").asText();
        String memberName = jsonNode.get("kakao_account").get("profile").get("nickname").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String provider = "kakao";

        System.out.println(memberName + "////" +email + "////" + providerId);

        return new OauthMemberInfoImpl(memberName, email, providerId, provider);
    }
}

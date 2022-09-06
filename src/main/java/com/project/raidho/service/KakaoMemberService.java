package com.project.raidho.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.member.MemberRole;
import com.project.raidho.domain.member.dto.MemberDto;
import com.project.raidho.domain.member.dto.OauthLoginResponseDto;
import com.project.raidho.domain.oauthMemberInfo.OauthMemberInfoImpl;
import com.project.raidho.domain.token.dto.JwtTokenDto;
import com.project.raidho.jwt.JwtTokenProvider;
import com.project.raidho.repository.MemberRepository;
import com.project.raidho.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final JwtTokenProvider jwtTokenProvider;

    // 카카오 로그인
    @Transactional
    public MemberDto kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. auth code 로 (Kakao -> Resource Server 접속이 가능한 ) accessToken 요청
        String KakaoResourceToken = getResourceToken(code); // 받은 인가코드를 통해 토큰을 받아옴
        OauthMemberInfoImpl kakaoMemberInfo = getKakaoMemberInfo(KakaoResourceToken); // Resource Server 에서 유저 정보 가져옴
        Member kakaoMember = joinMemberShip(kakaoMemberInfo); // 회원가입
        raidhoJwtTokenGenerate(kakaoMember, response);
        return MemberDto.builder().member(kakaoMember).build();
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
        body.add("redirect_uri", "https://raidho.site/login/oauth2/code/kakao");
        // body.add("redirect_uri", kakaoRedirect);
        body.add("code", code);

        //POST 요청 보냄
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

        String providerId = jsonNode.get("id").asText();
        String memberName = jsonNode.get("kakao_account").get("profile").get("nickname").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String provider = "kakao";

        return new OauthMemberInfoImpl(memberName, email, providerId, provider);
    }

    // Raidho 회원가입 (회원정보 존재시 바로 로그인)
    private Member joinMemberShip(OauthMemberInfoImpl kakaoMemberInfo) {

        String provider = kakaoMemberInfo.getProvider();
        String providerId = kakaoMemberInfo.getProviderId();
        String memberName = kakaoMemberInfo.getMemberName();
        Member kakaoMember = memberRepository.findByProviderId(providerId)
                .orElse(null);
        // 회원가입이 되어있지 않으면 Null
        if (kakaoMember == null) {
            String email = kakaoMemberInfo.getEmail();
            String memberImage = null; // Todo :: default image 필요
            String memberIntro = "인사말을 등록해주세요.";
            MemberRole role = MemberRole.USER;

            Member member = Member.builder()
                    .memberName(memberName)
                    .email(email)
                    .memberImage(memberImage)
                    .memberIntro(memberIntro)
                    .provider(provider)
                    .providerId(providerId)
                    .role(role)
                    .build();

            memberRepository.save(member);
            return member;
        }
        return kakaoMember;
    }

    // HttpHeaders
    private void raidhoJwtTokenGenerate(Member kakaoMember, HttpServletResponse response) {
        UserDetails userDetails = new PrincipalDetails(kakaoMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // SecurityContextHolder (Authentication (UserDetails))
        Member member = ((PrincipalDetails) authentication.getPrincipal()).getMember();

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateTokenDto(member);
        String accessToken = jwtTokenDto.getAuthorization();
        String refreshToken = jwtTokenDto.getRefreshToken();

        response.addHeader("Authorization", accessToken);
        response.addHeader("RefreshToken", refreshToken);

//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("Authorization", accessToken);
//        httpHeaders.add("RefreshToken", refreshToken);
//
//        return httpHeaders;

    }
}

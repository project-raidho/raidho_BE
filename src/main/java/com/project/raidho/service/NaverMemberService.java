package com.project.raidho.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.member.MemberRole;
import com.project.raidho.domain.member.dto.OauthLoginResponseDto;
import com.project.raidho.domain.oauthMemberInfo.OauthMemberInfoImpl;
import com.project.raidho.domain.token.dto.JwtTokenDto;
import com.project.raidho.jwt.JwtTokenProvider;
import com.project.raidho.repository.MemberRepository;
import com.project.raidho.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class NaverMemberService {



    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    String naverClientSecret;

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    public OauthLoginResponseDto naverLogin(String code, String state, HttpServletResponse response) throws JsonProcessingException {

        String NaverResourceToken = getResourceToken(code, state);
        OauthMemberInfoImpl naverMemberInfo = getNaverMemberInfo(NaverResourceToken);
        Member naverMember = joinMemberShip(naverMemberInfo);
        HttpHeaders httpHeaders = raidhoJwtTokenGenerate(naverMember,response);
        return new OauthLoginResponseDto(httpHeaders, naverMember);
    }

    private String getResourceToken(String code, String state) throws JsonProcessingException {
        // naver Server 에서 받을 Http 를 만들기 위해 Http Header, body 생성
        // header 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", naverClientId);
        body.add("client_secret", naverClientSecret);
        body.add("code", code);
        body.add("state", state);
        //POST 요청 보냄
        HttpEntity<MultiValueMap<String, String>> naverResourceTokenRequest = new HttpEntity<>(body, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverResourceTokenRequest,
                String.class
        );

        // naver Server accessToken response (JSON)
        String ResponseResourceToken = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper(); // json 형태의 객체 생성 (읽는 공간 생성)
        JsonNode jsonNode = objectMapper.readTree(ResponseResourceToken);
        return jsonNode.get("access_token").asText();
    }

    private OauthMemberInfoImpl getNaverMemberInfo(String NaverResourceToken) throws JsonProcessingException{

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer "+ NaverResourceToken);
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> naverResourceInfoRequest = new HttpEntity<>(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverResourceInfoRequest,
                String.class
        );

        String ResponseResourceInfo = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(ResponseResourceInfo);

        String providerId = jsonNode.get("response").get("id").asText();
        String memberName = jsonNode.get("response").get("nickname").asText();
        String email = jsonNode.get("response").get("email").asText();
        String provider = "naver";

        return new OauthMemberInfoImpl(memberName, email, providerId, provider);
    }

    private Member joinMemberShip(OauthMemberInfoImpl naverMemberInfo) {
        String provider = naverMemberInfo.getProvider();
        String providerId = naverMemberInfo.getProviderId();
        String memberName = naverMemberInfo.getMemberName();
        Member naverMember = memberRepository.findMember(provider, providerId, memberName)
                .orElse(null);

        // 회원가입이 되어있지 않으면 Null
        if (naverMember == null) {
            String email = naverMemberInfo.getEmail();
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
        return naverMember;
    }

    private HttpHeaders raidhoJwtTokenGenerate(Member naverMember, HttpServletResponse response) {
        UserDetails userDetails = new PrincipalDetails(naverMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // SecurityContextHolder (Authentication (UserDetails))

        // PrincipalDetails principalDetails = ((PrincipalDetails) authentication.getPrincipal());

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateTokenDto(naverMember);
        String accessToken = jwtTokenDto.getAuthorization();
        String refreshToken = jwtTokenDto.getRefreshToken();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", accessToken);
        httpHeaders.add("RefreshToken", refreshToken);

        return httpHeaders;

    }

}

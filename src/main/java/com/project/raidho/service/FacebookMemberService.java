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
public class FacebookMemberService {

    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    String facebookClientId;

    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    String facebookClientSecret;

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    public MemberDto facebookLogin(String code, HttpServletResponse response) throws JsonProcessingException {

        String facebookResourceToken = getResourceToken(code);
        OauthMemberInfoImpl facebookMemberInfo = getFacebookMemberInfo(facebookResourceToken);
        Member facebookMember = joinMemberShip(facebookMemberInfo);
        raidhoJwtTokenGenerate(facebookMember,response);
        return MemberDto.builder().member(facebookMember).build();
    }

    private String getResourceToken(String code) throws JsonProcessingException {
        // naver Server 에서 받을 Http 를 만들기 위해 Http Header, body 생성
        // header 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", facebookClientId);
        body.add("client_secret", facebookClientSecret);
        body.add("redirect_uri", "http://localhost:3000/login/oauth2/code/facebook");
        body.add("code", code);
        //POST 요청 보냄
        HttpEntity<MultiValueMap<String, String>> facebookResourceTokenRequest = new HttpEntity<>(body, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://graph.facebook.com/v3.0/oauth/access_token",
                HttpMethod.POST,
                facebookResourceTokenRequest,
                String.class
        );

        // naver Server accessToken response (JSON)
        String ResponseResourceToken = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper(); // json 형태의 객체 생성 (읽는 공간 생성)
        JsonNode jsonNode = objectMapper.readTree(ResponseResourceToken);
        return jsonNode.get("access_token").asText();
    }

    private OauthMemberInfoImpl getFacebookMemberInfo(String FacebookResourceToken) throws JsonProcessingException{

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer "+ FacebookResourceToken);
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> facebookResourceInfoRequest = new HttpEntity<>(httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://graph.facebook.com/v3.0/me?fields=id,first_name,middle_name,last_name,name,email,verified",
                HttpMethod.POST,
                facebookResourceInfoRequest,
                String.class
        );

        String ResponseResourceInfo = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(ResponseResourceInfo);

        String providerId = jsonNode.get("id").asText();
        String memberName = jsonNode.get("name").asText();
        String email = jsonNode.get("email").asText();
        String provider = "facebook";

        return new OauthMemberInfoImpl(memberName, email, providerId, provider);
    }

    private Member joinMemberShip(OauthMemberInfoImpl facebookMemberInfo) {
        String provider = facebookMemberInfo.getProvider();
        String providerId = facebookMemberInfo.getProviderId();
        String memberName = facebookMemberInfo.getMemberName();
        Member facebookMember = memberRepository.findMember(provider, providerId, memberName)
                .orElse(null);

        // 회원가입이 되어있지 않으면 Null
        if (facebookMember == null) {
            String email = facebookMemberInfo.getEmail();
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
        return facebookMember;
    }
    private void raidhoJwtTokenGenerate(Member facebookMember, HttpServletResponse response) {
        UserDetails userDetails = new PrincipalDetails(facebookMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // SecurityContextHolder (Authentication (UserDetails))

        // PrincipalDetails principalDetails = ((PrincipalDetails) authentication.getPrincipal());
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


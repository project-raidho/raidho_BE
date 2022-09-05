package com.project.raidho.jwt;

import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.token.RefreshToken;
import com.project.raidho.domain.token.dto.JwtTokenDto;
import com.project.raidho.repository.RefreshRepository;
import com.project.raidho.security.PrincipalDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 10 * 30; // accessToken exp 10분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // RefreshToken 7일

    private final Key key;

    // private final PrincipalDetails principalDetails;

    private final RefreshRepository refreshRepository;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RefreshRepository refreshRepository) {
        // Key 만들기
        // 로직 안에서는 byte 단위의 secretKey 를 만들어 주어야 한다.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // 알고리즘 선택
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.refreshRepository = refreshRepository;
    }

    // jwt Token 생성
    public JwtTokenDto generateTokenDto (Member member) {

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME); // accessToken exp
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME); // refreshToken exp

        // jwt headers
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        headers.put("typ", "Bearer");

        // jwt payload
        Map<String, String> claims = new HashMap<>();
        claims.put("memberName", member.getMemberName());
        claims.put("memberEmail", member.getEmail());

        // jwt secret accessToken 생성
        String accessToken = Jwts.builder()
                .setHeader(headers)
                .setIssuer("raidho")
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // jwt secret refreshToken 생성
        String refreshToken = Jwts.builder()
                .setHeader(headers)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshTokenSave = RefreshToken.builder()
                .id(member.getId())
                .refreshToken(refreshToken)
                .build();

        refreshTokenSave.updateTokenValue(refreshToken);

        refreshRepository.save(refreshTokenSave);

        // return
        return JwtTokenDto.builder()
                .authorization(BEARER_TYPE + " " + accessToken)
                .refreshToken(BEARER_TYPE + " " + refreshToken)
                .build();
    }
}

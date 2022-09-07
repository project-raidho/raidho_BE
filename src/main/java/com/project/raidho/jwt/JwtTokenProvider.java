package com.project.raidho.jwt;

import com.project.raidho.domain.member.Member;
import com.project.raidho.domain.token.RefreshToken;
import com.project.raidho.domain.token.dto.JwtTokenDto;
import com.project.raidho.repository.RefreshRepository;
import com.project.raidho.security.PrincipalDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Todo :: 모든 Class log 남기기

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 10 * 30; // accessToken exp 10분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // RefreshToken 7일

    private final Key key;

    private final RefreshRepository refreshRepository;
    private final PrincipalDetailsService principalDetailsService;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RefreshRepository refreshRepository, PrincipalDetailsService principalDetailsService) {
        // Key 만들기
        // 로직 안에서는 byte 단위의 secretKey 를 만들어 주어야 한다.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // 알고리즘 선택
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.refreshRepository = refreshRepository;
        this.principalDetailsService = principalDetailsService;
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
        claims.put("providerId", member.getProviderId());

        // jwt secret accessToken 생성
        String accessToken = Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
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

    public boolean validationToken(String jwtToken) {
        try {
            // 위에서 암호환한 Jwts를 복호화 해줌
            // 위에서 signWith key 를 활용하여 암호화 했으므로 복호활 할 setSigningKey 에도 동일한 key 값을 넣어줌
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalStateException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // header request 에서 전달받은 accessToken 으로 정보 확인하기. --> 여기 원해 권한 정보 가져오는데 지금은 안쓰니까 나중에 다시 체크해봐.
    public Authentication getAuthentication(String jwtToken) {
        Claims claims = parseClaims(jwtToken);
        String providerId = (String) claims.get("providerId"); // 전달받은 payload 에 key 값이 "memberEmail" 을 찾아 값을 가져옴
        UserDetails userDetails = principalDetailsService.loadUserByUsername(providerId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰 복호화
    // Todo :: 나 이 부분 이해 안되..
    private Claims parseClaims(String jwtToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}

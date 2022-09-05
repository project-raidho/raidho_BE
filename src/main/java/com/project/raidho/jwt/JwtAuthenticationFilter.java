package com.project.raidho.jwt;

import com.project.raidho.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // Filter 나 GenericFilterBean 이라는 스프링 확장 필터에서
                                                                    // 서블릿 실행 시 요청이 들어오면 다시 필터부터 동작이 되는데
                                                                    // OncePerRequestFilter 를 사용하면 사용자 요청 한번에 필터를 딱 한번만 돈다.
                                                                    // @Override doFilterInternal 를 구현해야한다.

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_AUTHORIZATION_HEADER = "RefreshToken";
    public static final String BEARER_PREFIX = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1) HttpServletRequest request 에서 Header(jwtToken)을 획득한다.
        String jwtToken = resolveToken(request);

        if (StringUtils.hasText(jwtToken) && jwtTokenProvider.validationToken(jwtToken)) { // jwtToken 에 값이 있고, 토큰 유형성 검증을 통과했을때..
            // jwtToken 으로 부터 Authentication 객체 얻어오기
            Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken); // 데이터베이스에 조회
            // 받아온 Authentication 객체 SecurityContextHolder 에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    // header 에서 토큰을 뽑는 메서드
    private String resolveToken(HttpServletRequest request) {
        // authorization 헤더에서 토큰 추출
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER); // Client 에서 accessToken 을 받아 올때 Authorization
        // 접두사 분리
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) { // 값이 있고 && 앞 철자가 Bearer 일때 true
            return bearerToken.substring(7); // "Bearer " + 토큰 정보에서 "Bearer " 를 땜
        }
        return null;
    }
}

package com.project.raidho.config;

import com.project.raidho.jwt.JwtAuthenticationFilter;
import com.project.raidho.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // origin
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // method
        configuration.setAllowedMethods(Arrays.asList("*"));
        // header
        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setAllowCredentials(true);

        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Authorization, RefreshToken, x-xsrf-token, Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, " +
                "Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers", "Access-Control-Allow-Origin"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource());
        http
                .httpBasic().disable()
                .csrf().disable()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .headers().frameOptions().disable() // h2-console 화면을 사용하기 위해 해당 옵션들을 disable 합니다.

                .and()
                .authorizeRequests() // URL 별 권한 관리를 설정하는 옵션의 시작점이다.
                // authorizeRequests 가 선언되어야만 antMatchers 옵션을 사용할 수 있다.

                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**","/login/**","/favicon.ico").permitAll()
                //.antMatchers("/api/v1/**").hasRole(Role.USER.name())
                // .antMatchers -> 권한 관리 대상을 지정하는 옵션이다.
                // URL, HTTP 메소드별로 관리가 가능하다.
                // "/" 등 지정된 URL 들은 permitAll() 옵션을 통해 전체 열람 권한을 주었습니다.
                // POST 메소드이면서 "/api/v1/**" 주소를 가진 API 는 USER 권한을 가진 사람만 가능하도록 했다.

                .anyRequest().authenticated(); // 설정된 값들 이외 나머지 URL 들을 나타낸다. (인증된 사용자들만)

//                .and()
//                .logout()
//                .logoutSuccessUrl("/") // 로그아웃 기능에 대한 여러 설정의 진입점입니다.
//                // 로그아웃 성공 시 "/" 주소로 이동합니다.
//
//                .and()
//                .oauth2Login() // OAuth2 로그인 기능에 대한 여러 설정의 진입점입니다.
//                .userInfoEndpoint(); // OAuth2 로그인 성공 이후 사용자 정보를 가져올 떄의 설정들을 담당합니다.
//                .userService(customOAuth2UserService); // 소셜 로그인 성공 시 후속 조치를 진행할 MemberService 인터페이스의 구현체를 등록합니다.
        // 리소스 서버 (즉, 소셜 서비스들) 에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시할 수 있습니다.

        return http.build();

    }
}

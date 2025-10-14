package project.landmark.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project.landmark.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CSRF 비활성화 (REST API 사용 시 필수)
                .csrf(csrf -> csrf.disable())

                // ✅ 세션 사용 안 함 (JWT 기반 인증 구조)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ 요청별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 🔓 인증 없이 접근 가능한 경로들 (테스트/정적 리소스)
                        .requestMatchers(
                                "/",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/achievement_test.html",  // ✅ 업적 테스트 페이지 허용
                                "/rankingtest.html",       // ✅ 랭킹 테스트 페이지 허용
                                "/api/auth/**",            // 로그인/회원가입 관련 API
                                "/api/ranking/**",         // 랭킹 관련 API

                                // ✅ 업적 관련 API 전체 허용 (CRUD + unlock + user조회)
                                "/api/achievements/**"
                        ).permitAll()

                        // 🔒 그 외 경로는 인증 필요
                        .anyRequest().authenticated()
                )

                // ✅ JWT 인증 필터 등록 (UsernamePasswordAuthenticationFilter 앞에 실행)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ 비밀번호 암호화기 (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

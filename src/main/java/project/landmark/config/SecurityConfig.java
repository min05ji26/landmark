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

// 💡 CORS 설정 적용을 위해 추가
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // 🚨 [중요] 아까 WebConfig에서 만든 CORS 설정을 여기서도 적용해 줘야 합니다!
                .cors(Customizer.withDefaults())

                // ✅ 세션 사용 안 함
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ 요청별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 🔓 인증 없이 접근 가능한 경로들
                        .requestMatchers(
                                "/",
                                "/favicon.ico",
                                "/static/css/**",
                                "/js/**",
                                "/images/**",
                                "/achievement_test.html",
                                "/rankingtest.html",
                                "/api/auth/**",
                                "/api/ranking/**",
                                "/api/achievements/**",

                                // ★ [여기 추가!] 홈 화면 데이터 조회 허용
                                "/api/home"
                        ).permitAll()

                        // 🔒 그 외 경로는 인증 필요
                        .anyRequest().authenticated()
                )

                // ✅ JWT 인증 필터 등록
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
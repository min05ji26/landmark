package project.landmark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // 🚨 [수정] allowedOrigins 대신 allowedOriginPatterns 사용
                        // 이렇게 하면 allowCredentials(true)와 함께 와일드카드(*) 패턴 사용 가능
                        .allowedOriginPatterns(
                                "http://localhost:3000",
                                "http://localhost:5173",
                                "http://localhost:8081",
                                "http://localhost:8080",
                                "http://192.168.219.140:*" // 👈 IP 대역 허용 (유동적일 때 유용)
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true); // 인증 정보(토큰 등) 허용
            }
        };
    }
}
package project.landmark.config; // 패키지 이름은 본인 프로젝트에 맞게!

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
                        // 1. 별표(*) 대신 리액트 주소를 명확히 적어줍니다.
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        // 2. 이 옵션이 있어야 나중에 로그인할 때 쿠키/토큰을 주고받을 수 있습니다.
                        .allowCredentials(true);
            }
        };
    }
}
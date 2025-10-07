package project.landmark.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.security.Key;

@Component
public class JwtTokenProvider {

    // ✅ 토큰 서명용 비밀키 (실제 서비스에서는 환경변수나 yml로 분리)
    private final Key key = Keys.hmacShaKeyFor("my-super-secret-key-for-jwt-signing-2025".getBytes());
    private final long validityInMs = 1000L * 60 * 60; // 1시간 유효

    // ✅ 토큰 생성
    public String createToken(Long userId, String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("id", userId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ✅ 토큰에서 username 추출
    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}

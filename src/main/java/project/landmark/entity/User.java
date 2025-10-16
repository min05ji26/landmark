package project.landmark.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity // JPA 엔티티임을 표시
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 자동 생성
    private Long userId;

    @Column(nullable = false, unique = true, length = 50) // 로그인용 ID
    private String username;

    @Column(nullable = false) // 암호화된 비밀번호
    private String password;

    @Column(nullable = false, unique = true, length = 100) // 이메일
    private String email;

    @Column(nullable = false, length = 30) // 닉네임
    private String nickname;

    private Long totalSteps = 0L; // 총 걸음 수 (default 0)

    private String representativeTitle; // 대표 칭호

    private LocalDateTime createdAt;  // 가입일
    private LocalDateTime updatedAt;  // 마지막 수정일

}

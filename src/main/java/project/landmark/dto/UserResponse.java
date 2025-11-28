package project.landmark.dto;

import lombok.*;
import project.landmark.entity.User;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private Long totalSteps;
    private String representativeTitle;
    private int level; // 👈 추가된 부분 (홈 화면 프로그레스바 계산용)
    private LocalDateTime createdAt;

    // User 엔티티를 UserResponse로 변환하는 메서드 (편의용)
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .totalSteps(user.getTotalSteps())
                .representativeTitle(user.getRepresentativeTitle())
                .level(user.getLevel()) // 레벨 데이터 포함
                .createdAt(user.getCreatedAt())
                .build();
    }
}
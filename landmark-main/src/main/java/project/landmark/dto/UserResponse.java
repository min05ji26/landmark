package project.landmark.dto;


import lombok.*;


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
    private LocalDateTime createdAt;
}
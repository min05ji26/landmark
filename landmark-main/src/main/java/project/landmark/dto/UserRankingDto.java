package project.landmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRankingDto {
    private Long userId;
    private String nickname;
    private Long totalSteps;
    private int rank; // 서비스 로직에서 채움
}

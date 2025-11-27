package project.landmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRankingDto {

    private Long userId;
    private String nickname;
    private Long totalSteps;
    private int rank;

    // ✅ JPQL 쿼리에서 new 로 생성할 때 사용되는 생성자 (3개 인자)
    public UserRankingDto(Long userId, String nickname, Long totalSteps) {
        this.userId = userId;
        this.nickname = nickname;
        this.totalSteps = totalSteps;
    }
}

package project.landmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class UserRankingDto {

    private Long userId;
    private String nickname;
    private Long totalSteps;
    private int rank;
    private String representativeTitle; // 👈 추가된 부분 (칭호)

    // 생성자 수정: 순서대로 (userId, nickname, totalSteps, rank, representativeTitle)
    public UserRankingDto(Long userId, String nickname, Long totalSteps, int rank, String representativeTitle) {
        this.userId = userId;
        this.nickname = nickname;
        this.totalSteps = totalSteps;
        this.rank = rank;
        this.representativeTitle = representativeTitle;
    }
}
package project.landmark.dto;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
//남에게 보이는 내 프로필
public class ProfileResponseDto {
    private String nickname;           // 닉네임

    private Long totalSteps;           // 총 걸음 수
    private String representativeTitle; // 대표 칭호
    private List<FriendDto> friends;   // 친구 목록
    private List<String> titles;       // 획득한 칭호 이름 리스트
}

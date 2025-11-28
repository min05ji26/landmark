package project.landmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ✅ 랜드마크 진행 상황 DTO
 * - 프론트가 목록/상세 화면을 그릴 때 필요한 정보만 담아서 내려줌
 * - 백엔드는 이 DTO만 반환하고, UI 표현(색깔, 자물쇠, 알람)은 전부 프론트에서 처리
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandmarkProgressDto {

    private Long id;              // 랜드마크 ID
    private String name;          // 랜드마크 이름
    private String imageUrl;      // 랜드마크 이미지 URL (없으면 null 가능)

    private Long requiredSteps;   // 해금에 필요한 걸음 수
    private Long currentSteps;    // 유저의 현재 걸음 수 (12000 등, 실제 값 그대로)

    private int progressPercent;  // 진행률(%). 12000/5000이어도 100으로 고정
    private boolean unlocked;     // 해금 여부 (true면 색 정상, false면 회색+자물쇠)
}
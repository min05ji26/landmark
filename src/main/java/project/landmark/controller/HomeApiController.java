package project.landmark.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.landmark.dto.ApiResponse;
import project.landmark.dto.UserRankingDto;
import project.landmark.entity.Landmark;
import project.landmark.entity.User;
import project.landmark.service.LandmarkService;
import project.landmark.service.RankingService;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeApiController {

    private final LandmarkService landmarkService;
    private final RankingService rankingService;

    // ✅ 홈 화면 데이터 조회 API
    @GetMapping
    public ResponseEntity<ApiResponse<HomeResponse>> getHomeData(@AuthenticationPrincipal User user) {

        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("로그인이 필요합니다."));
        }

        // 1. 내 랭킹 계산하기
        int myRank = 0; // 기본값 0 (순위권 밖)
        List<UserRankingDto> weeklyRanking = rankingService.calculateWeeklyRanking();

        for (UserRankingDto dto : weeklyRanking) {
            // 닉네임이 같으면 내 등수로 설정
            if (dto.getNickname().equals(user.getNickname())) {
                myRank = dto.getRank();
                break;
            }
        }
        // 🚨 수정: 랭킹에 없으면 억지로 전체인원+1 하지 않고 0으로 둠 (프론트에서 '-' 처리)

        // 2. 현재 랜드마크 & 목표 랜드마크 계산하기
        List<Landmark> allLandmarks = landmarkService.findAll();
        Landmark currentLandmark = null;
        Landmark nextLandmark = null;

        Long currentSteps = user.getTotalSteps() != null ? user.getTotalSteps() : 0L;

        // DB에 랜드마크가 없을 경우 대비
        if (allLandmarks.isEmpty()) {
            nextLandmark = Landmark.builder().name("데이터 없음").requiredSteps(100000L).build();
        } else {
            for (Landmark lm : allLandmarks) {
                if (currentSteps >= lm.getRequiredSteps()) {
                    currentLandmark = lm; // 통과한 곳 중 가장 높은 곳
                } else {
                    nextLandmark = lm; // 아직 못 간 곳 중 가장 낮은 곳 (목표)
                    break;
                }
            }
            // 모든 랜드마크를 깼다면 마지막 랜드마크를 목표로 유지
            if (nextLandmark == null) {
                nextLandmark = allLandmarks.get(allLandmarks.size() - 1);
            }
        }

        // 🚨 수정: 시작점을 '집'이 아니라 DB의 첫 번째 랜드마크 입구로 설정
        if (currentLandmark == null) {
            if (!allLandmarks.isEmpty()) {
                // 예: 해운대 입구
                Landmark first = allLandmarks.get(0);
                currentLandmark = Landmark.builder().name(first.getName() + " 입구").build();
            } else {
                currentLandmark = Landmark.builder().name("시작점").build();
            }
        }

        // 3. 응답 데이터 조립
        HomeResponse response = HomeResponse.builder()
                .userInfo(HomeResponse.UserInfo.builder()
                        .nickname(user.getNickname())
                        .totalSteps(currentSteps)
                        .representativeTitle(user.getRepresentativeTitle())
                        .build())
                .rankingInfo(HomeResponse.RankingInfo.builder()
                        .rank(myRank)
                        .build())
                .landmarkInfo(HomeResponse.LandmarkInfo.builder()
                        .name(nextLandmark.getName()) // 목표 건물
                        .requiredSteps(nextLandmark.getRequiredSteps())
                        .currentSteps(currentSteps)
                        .build())
                .currentLocationName(currentLandmark.getName())
                .build();

        return ResponseEntity.ok(ApiResponse.ok("홈 데이터 조회 성공", response));
    }

    // --- DTO 클래스 ---
    @Getter
    @Builder
    public static class HomeResponse {
        private UserInfo userInfo;
        private RankingInfo rankingInfo;
        private LandmarkInfo landmarkInfo;
        private String currentLocationName;

        @Getter @Builder
        public static class UserInfo {
            private String nickname;
            private Long totalSteps;
            private String representativeTitle;
        }

        @Getter @Builder
        public static class RankingInfo {
            private int rank;
        }

        @Getter @Builder
        public static class LandmarkInfo {
            private String name;
            private Long requiredSteps;
            private Long currentSteps;
        }
    }
}
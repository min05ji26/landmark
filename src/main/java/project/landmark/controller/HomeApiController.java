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
import project.landmark.dto.UserRankingDto; // 👈 추가됨
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

        // 1. 내 랭킹 계산하기 (실제 로직 적용)
        int myRank = 0; // 순위권 밖일 경우 0 또는 적절한 값
        List<UserRankingDto> weeklyRanking = rankingService.calculateWeeklyRanking();

        for (UserRankingDto dto : weeklyRanking) {
            // 닉네임이 같으면 내 등수로 설정
            if (dto.getNickname().equals(user.getNickname())) {
                myRank = dto.getRank();
                break;
            }
        }

        // 랭킹에 데이터가 없거나 순위권 밖이면 표시할 기본값 (예: 999위)
        if (myRank == 0) {
            myRank = weeklyRanking.size() + 1;
        }

        // 2. 현재 랜드마크 & 목표 랜드마크 계산하기
        List<Landmark> allLandmarks = landmarkService.findAll();
        Landmark currentLandmark = null;
        Landmark nextLandmark = null;

        // 걸음 수에 따라 내가 어디 있는지 찾기
        for (Landmark lm : allLandmarks) {
            if (user.getTotalSteps() >= lm.getRequiredSteps()) {
                currentLandmark = lm; // 통과한 곳 중 가장 높은 곳
            } else {
                nextLandmark = lm; // 아직 못 간 곳 중 가장 낮은 곳 (목표)
                break;
            }
        }

        // 만약 모든 랜드마크를 다 깼다면?
        if (nextLandmark == null && !allLandmarks.isEmpty()) {
            nextLandmark = allLandmarks.get(allLandmarks.size() - 1); // 마지막 랜드마크 유지
        }
        // 만약 아직 하나도 못 깼다면?
        if (currentLandmark == null) {
            // 임시 객체 생성 (시작점)
            currentLandmark = Landmark.builder().name("집").build();
        }


        // 3. 응답 데이터 조립
        HomeResponse response = HomeResponse.builder()
                .userInfo(HomeResponse.UserInfo.builder()
                        .nickname(user.getNickname())
                        .totalSteps(user.getTotalSteps())
                        .representativeTitle(user.getRepresentativeTitle())
                        .build())
                .rankingInfo(HomeResponse.RankingInfo.builder()
                        .rank(myRank)
                        .build())
                .landmarkInfo(HomeResponse.LandmarkInfo.builder()
                        .name(nextLandmark.getName()) // 목표 건물
                        .requiredSteps(nextLandmark.getRequiredSteps())
                        .currentSteps(user.getTotalSteps())
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
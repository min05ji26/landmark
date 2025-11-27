package project.landmark.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.landmark.dto.ApiResponse;
import project.landmark.entity.Landmark;
import project.landmark.entity.User;
import project.landmark.repository.LandmarkRepository; // Repository로 변경
import project.landmark.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final LandmarkRepository landmarkRepository; // Service 대신 Repository 직접 사용 (간단 조회)
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<HomeResponse>> getHomeData() {
        // 1. 유저 정보 (ID 1번 김민민 강제 조회)
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다."));

        // 2. 랭킹 계산 (걸음 수가 0이면 랭킹 없음으로 처리)
        int myRank = 0; // 0이면 화면에 안 뜨게 할 예정
        if (user.getTotalSteps() > 0) {
            myRank = 2; // 걸음이 조금이라도 있으면 2등 (임시)
        }

        // 3. 랜드마크 계산 (무조건 해운대부터 시작)
        // DB에 있는 모든 랜드마크를 가져와서 '필요 걸음 수' 순서대로 정렬합니다.
        List<Landmark> allLandmarks = landmarkRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Landmark::getRequiredSteps))
                .collect(Collectors.toList());

        Landmark currentLandmark = Landmark.builder().name("시작점").build(); // 기본값
        Landmark nextLandmark = null;

        if (!allLandmarks.isEmpty()) {
            // "내가 아직 달성하지 못한 랜드마크" 중 가장 쉬운 놈을 찾음 (그게 바로 목표!)
            nextLandmark = allLandmarks.stream()
                    .filter(lm -> lm.getRequiredSteps() > user.getTotalSteps())
                    .findFirst()
                    .orElse(null);

            // 만약 목표가 없으면(다 깼으면) 마지막 랜드마크 유지
            if (nextLandmark == null) {
                nextLandmark = allLandmarks.get(allLandmarks.size() - 1);
            }

            // "내가 이미 달성한 랜드마크" 중 가장 어려운 놈 (현재 위치)
            Landmark finalCurrentLandmark = currentLandmark;
            currentLandmark = allLandmarks.stream()
                    .filter(lm -> lm.getRequiredSteps() <= user.getTotalSteps())
                    .max(Comparator.comparingLong(Landmark::getRequiredSteps))
                    .orElse(finalCurrentLandmark);
        } else {
            // DB가 비었을 때 비상용
            nextLandmark = Landmark.builder().name("해운대(데이터없음)").requiredSteps(20000L).build();
        }

        // 4. 응답 만들기
        HomeResponse response = HomeResponse.builder()
                .userInfo(HomeResponse.UserInfo.builder()
                        .nickname(user.getNickname())
                        .totalSteps(user.getTotalSteps())
                        .representativeTitle(user.getRepresentativeTitle())
                        .build())
                .rankingInfo(HomeResponse.RankingInfo.builder()
                        .rank(myRank) // 0 또는 2
                        .build())
                .landmarkInfo(HomeResponse.LandmarkInfo.builder()
                        .name(nextLandmark.getName())           // 목표 (해운대)
                        .requiredSteps(nextLandmark.getRequiredSteps()) // 20000
                        .currentSteps(user.getTotalSteps())
                        .build())
                .currentLocationName(currentLandmark.getName()) // 현재 위치
                .build();

        return ResponseEntity.ok(ApiResponse.ok("조회 성공", response));
    }

    @Getter @Builder
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
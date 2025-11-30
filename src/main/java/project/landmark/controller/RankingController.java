package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.landmark.dto.ApiResponse;
import project.landmark.dto.UserRankingDto;
import project.landmark.service.RankingService;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
// 🚨 [수정] @CrossOrigin(origins = "*") 삭제함 (SecurityConfig와 충돌 방지)
public class RankingController {

    private final RankingService rankingService;

    // ✅ 주간 랭킹
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<List<UserRankingDto>>> getWeeklyRanking() {
        List<UserRankingDto> ranking = rankingService.calculateWeeklyRanking();
        return ResponseEntity.ok(ApiResponse.ok("주간 랭킹 조회 성공", ranking));
    }

    // ✅ 월간 랭킹
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<List<UserRankingDto>>> getMonthlyRanking() {
        List<UserRankingDto> ranking = rankingService.calculateMonthlyRanking();
        return ResponseEntity.ok(ApiResponse.ok("월간 랭킹 조회 성공", ranking));
    }

    // ✅ 구별 랭킹
    @GetMapping("/district/{district}")
    public ResponseEntity<ApiResponse<List<UserRankingDto>>> getDistrictRanking(@PathVariable String district) {
        List<UserRankingDto> ranking = rankingService.calculateDistrictRanking(district);
        return ResponseEntity.ok(ApiResponse.ok(district + " 랭킹 조회 성공", ranking));
    }

    // ✅ 친구 랭킹 (username 기준으로 변경)
    @GetMapping("/friends/{username}")
    public ResponseEntity<ApiResponse<List<UserRankingDto>>> getFriendRanking(@PathVariable String username) {
        List<UserRankingDto> ranking = rankingService.calculateFriendRankingByUsername(username);
        return ResponseEntity.ok(ApiResponse.ok(username + "의 친구 랭킹 조회 성공", ranking));
    }
}
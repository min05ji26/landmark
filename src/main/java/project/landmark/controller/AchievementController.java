package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.landmark.dto.AchievementStatusDto;
import project.landmark.dto.ApiResponse;
import project.landmark.entity.Achievement;
import project.landmark.entity.User;
import project.landmark.service.AchievementService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    // ✅ [추가] 업적 목록 + 내 달성 여부 조회
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<AchievementStatusDto>>> getAchievementList(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok("업적 목록 조회 성공",
                achievementService.getAchievementsWithStatus(user.getUsername())));
    }

    // ✅ [추가] 업적 획득 버튼 클릭 (Claim)
    @PostMapping("/{id}/claim")
    public ResponseEntity<ApiResponse<String>> claimAchievement(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        String achievementName = achievementService.claimAchievement(user.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok("업적 획득 성공", achievementName));
    }

    // --- 기존 API들 ---
    @PostMapping
    public ResponseEntity<Achievement> create(@RequestBody Achievement achievement) {
        return ResponseEntity.ok(achievementService.createAchievement(achievement));
    }

    @GetMapping
    public ResponseEntity<List<Achievement>> getAchievements(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(achievementService.getAchievements(sort, type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Achievement> getById(@PathVariable Long id) {
        return achievementService.getAchievement(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 기존 자동 해금용 (앱에선 사용 안 할 예정)
    @PostMapping("/unlock")
    public ResponseEntity<Map<String, String>> unlock(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(Map.of("message", "Manual claim only"));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Achievement>> getUserAchievements(@PathVariable String username) {
        return ResponseEntity.ok(achievementService.getUserAchievements(username));
    }
}
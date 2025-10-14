package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.landmark.entity.Achievement;
import project.landmark.service.AchievementService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

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

    @PutMapping("/{id}")
    public ResponseEntity<Achievement> update(@PathVariable Long id, @RequestBody Achievement updated) {
        return ResponseEntity.ok(achievementService.updateAchievement(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ 조건 기반 업적 달성 (username 기준)
    @PostMapping("/unlock")
    public ResponseEntity<Map<String, String>> unlock(@RequestBody Map<String, Object> request) {
        String username = request.get("username").toString();
        int currentSteps = Integer.parseInt(request.get("currentSteps").toString());

        String message = achievementService.unlockAchievements(username, currentSteps);
        return ResponseEntity.ok(Map.of("message", message));
    }

    // ✅ 사용자 업적 내역 조회 (username 기준)
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Achievement>> getUserAchievements(@PathVariable String username) {
        return ResponseEntity.ok(achievementService.getUserAchievements(username));
    }
}

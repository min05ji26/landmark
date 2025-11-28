package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.landmark.dto.ApiResponse;
import project.landmark.entity.User;
import project.landmark.service.StepService;

import java.util.Map;

@RestController
@RequestMapping("/api/steps")
@RequiredArgsConstructor
public class StepController {

    private final StepService stepService;

    // ✅ 걸음 수 동기화 API
    // 앱에서 10초마다 이 주소로 걸음 수를 보냅니다.
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<String>> syncSteps(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Integer> request
    ) {
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("로그인 필요"));
        }

        Integer steps = request.get("steps");
        if (steps == null) {
            steps = 0;
        }

        stepService.addSteps(user.getUsername(), steps);

        return ResponseEntity.ok(ApiResponse.ok("걸음 수 저장 완료", "Added: " + steps));
    }
}
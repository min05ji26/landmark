package project.landmark.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.landmark.dto.ApiResponse;
import project.landmark.entity.User;

@RestController
@RequestMapping("/api/user")
public class UserController {

    // ✅ 로그인된 사용자 정보 확인용 API
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<User>> getUserInfo(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("인증되지 않은 사용자입니다."));
        }
        return ResponseEntity.ok(ApiResponse.ok("유저 정보 조회 성공", user));
    }
}

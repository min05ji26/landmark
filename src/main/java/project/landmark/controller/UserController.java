package project.landmark.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.landmark.dto.ApiResponse;
import project.landmark.dto.UserResponse; // DTO 임포트 확인
import project.landmark.entity.User;

@RestController
@RequestMapping("/api/user")
public class UserController {

    // ✅ 내 정보 조회 (수정됨: 보안을 위해 UserResponse 반환)
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("인증되지 않은 사용자입니다."));
        }
        // User 엔티티 -> UserResponse DTO 변환 후 리턴
        return ResponseEntity.ok(ApiResponse.ok("유저 정보 조회 성공", UserResponse.from(user)));
    }
}
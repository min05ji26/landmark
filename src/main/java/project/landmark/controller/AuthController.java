package project.landmark.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.landmark.dto.*;
import project.landmark.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse resp = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("회원가입이 완료되었습니다.", resp));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("로그인 성공 (JWT 발급됨)", token));
    }

    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@RequestParam String username) {
        boolean isAvailable = authService.isUsernameAvailable(username);
        String msg = isAvailable ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.";
        return ResponseEntity.ok(ApiResponse.ok(msg, isAvailable));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<Boolean>> checkNickname(@RequestParam String nickname) {
        boolean isAvailable = authService.isNicknameAvailable(nickname);
        String msg = isAvailable ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다.";
        return ResponseEntity.ok(ApiResponse.ok(msg, isAvailable));
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        boolean isAvailable = authService.isEmailAvailable(email);
        String msg = isAvailable ? "사용 가능한 이메일입니다." : "이미 가입된 이메일입니다.";
        return ResponseEntity.ok(ApiResponse.ok(msg, isAvailable));
    }

    @GetMapping("/find-id")
    public ResponseEntity<ApiResponse<String>> findId(@RequestParam String nickname, @RequestParam String email) {
        String foundUsername = authService.findUsername(nickname, email);
        return ResponseEntity.ok(ApiResponse.ok("아이디 찾기 성공", foundUsername));
    }

    // ✅ [추가] 비밀번호 재설정 정보 확인
    @PostMapping("/check-reset-info")
    public ResponseEntity<ApiResponse<Void>> checkResetInfo(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        authService.validateForResetPassword(username, email);
        return ResponseEntity.ok(ApiResponse.ok("정보 확인 성공", null));
    }

    // ✅ [추가] 비밀번호 재설정 실행
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String newPassword = request.get("newPassword");
        authService.resetPassword(username, newPassword);
        return ResponseEntity.ok(ApiResponse.ok("비밀번호 재설정 성공", null));
    }
}
package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.landmark.dto.ApiResponse;
import project.landmark.dto.FriendDto;
import project.landmark.entity.User;
import project.landmark.service.FriendService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 내 친구 목록
    @GetMapping
    public ResponseEntity<ApiResponse<List<FriendDto>>> getFriends(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok("친구 목록 조회 성공", friendService.getMyFriends(user.getUsername())));
    }

    // 🚨 [수정] 친구 추가 요청 (바로 추가X -> 알림 발송O)
    @PostMapping
    public ResponseEntity<ApiResponse<String>> requestFriend(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request
    ) {
        String nickname = request.get("nickname");
        // addFriend 대신 requestFriend 호출
        friendService.requestFriend(user.getUsername(), nickname);
        return ResponseEntity.ok(ApiResponse.ok("친구 요청을 보냈습니다.", nickname));
    }

    @DeleteMapping("/{nickname}")
    public ResponseEntity<ApiResponse<String>> deleteFriend(
            @AuthenticationPrincipal User user,
            @PathVariable String nickname
    ) {
        friendService.deleteFriend(user.getUsername(), nickname);
        return ResponseEntity.ok(ApiResponse.ok("친구 삭제 성공", nickname));
    }
}
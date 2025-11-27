package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.landmark.service.FriendService;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 친구 요청
    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(@RequestParam Long fromUserId, @RequestParam Long toUserId) {
        friendService.sendFriendRequest(fromUserId, toUserId);
        return ResponseEntity.ok("친구 요청이 전송되었습니다.");
    }

    // 친구 요청 수락
    @PutMapping("/accept/{requestId}")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Long requestId) {
        friendService.acceptFriendRequest(requestId);
        return ResponseEntity.ok("친구 요청이 수락되었습니다.");
    }

    // 친구 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        friendService.deleteFriend(userId, friendId);
        return ResponseEntity.ok("친구가 삭제되었습니다.");
    }
}

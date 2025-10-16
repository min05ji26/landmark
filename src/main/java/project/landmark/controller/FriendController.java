package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 친구 목록 조회
    @GetMapping("/{userId}")
    public List<FriendDto> getFriends(@PathVariable Long userId) {
        return friendService.getFriends(userId);
    }

    // 친구 요청
    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendRequestDto dto) {
        friendService.sendRequest(dto);
        return ResponseEntity.ok("친구 요청이 전송되었습니다.");
    }

    // 친구 수락
    @PutMapping("/accept/{requestId}")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Long requestId) {
        friendService.acceptRequest(requestId);
        return ResponseEntity.ok("친구 요청이 수락되었습니다.");
    }

    // 친구 삭제
    @DeleteMapping("/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable Long friendId) {
        friendService.deleteFriend(friendId);
        return ResponseEntity.ok("친구가 삭제되었습니다.");
    }
}

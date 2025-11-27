package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.landmark.dto.FriendDto;
import project.landmark.dto.ProfileResponseDto;
import project.landmark.service.ProfileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    /* ✅ [1] 프로필 기본 정보 조회 */
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable Long userId) {
        ProfileResponseDto response = profileService.getProfileBasic(userId);
        return ResponseEntity.ok(response);
    }

    /* ✅ [2] 친구 목록 조회 */
    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<FriendDto>> getFriends(@PathVariable Long userId) {
        List<FriendDto> friends = profileService.getMyFriends(userId);
        return ResponseEntity.ok(friends);
    }

    /* ✅ [3] 보유 칭호 목록 조회 */
    @GetMapping("/{userId}/titles")
    public ResponseEntity<List<String>> getMyTitles(@PathVariable Long userId) {
        List<String> titles = profileService.getMyTitles(userId);
        return ResponseEntity.ok(titles);
    }

    /* ✅ [4] 대표 칭호 변경 (PUT /api/profile/title?userId=1&newTitle=오사카 정복자) */
    @PutMapping("/title")
    public ResponseEntity<String> updateTitle(@RequestParam Long userId, @RequestParam String newTitle) {
        profileService.updateTitle(userId, newTitle);
        return ResponseEntity.ok("대표 칭호가 '" + newTitle + "'로 변경되었습니다.");
    }

    /* ✅ [5] 걸음 수 추가 및 자동 레벨업 (PUT /api/profile/steps?userId=1&steps=3000) */
    @PutMapping("/steps")
    public ResponseEntity<String> addSteps(@RequestParam Long userId, @RequestParam Long steps) {
        String result = profileService.addSteps(userId, steps);
        return ResponseEntity.ok(result);
    }
}

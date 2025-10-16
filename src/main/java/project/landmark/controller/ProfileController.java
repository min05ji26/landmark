package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.landmark.dto.FriendDto;
import project.landmark.dto.ProfileResponseDto;
import project.landmark.service.ProfileService;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // 프로필 기본 정보 조회
    // 페이지 진입 시 기본 정보 (닉네임, 총 걸음 수, 대표 칭호)
    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> getProfileBasic(@PathVariable Long id) {
        ProfileResponseDto profile = profileService.getProfileBasic(id);
        return ResponseEntity.ok(profile);
    }

    // 친구 목록 조회
    // 프로필에서 친구 목록 버튼 클릭 시 호출
    @GetMapping("/{id}/friends")
    public ResponseEntity<List<FriendDto>> getMyFriends(@PathVariable Long id) {
        List<FriendDto> friends = profileService.getMyFriends(id);
        return ResponseEntity.ok(friends);
    }

    //보유 칭호 목록 조회
    //칭호 설정 페이지에서 호출
    @GetMapping("/{id}/titles")
    public ResponseEntity<List<String>> getMyTitles(@PathVariable Long id) {
        List<String> titles = profileService.getMyTitles(id);
        return ResponseEntity.ok(titles);
    }

    // [4] 대표 칭호 변경
    // ex) PUT /api/profile/1/title?newTitle=오사카 탐험가
    @PutMapping("/{id}/title")
    public ResponseEntity<String> updateTitle(@PathVariable Long id,
                                              @RequestParam String newTitle) {
        profileService.updateTitle(id, newTitle);
        return ResponseEntity.ok("대표 칭호가 변경되었습니다: " + newTitle);
    }
}

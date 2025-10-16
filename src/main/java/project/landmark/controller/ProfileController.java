package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.landmark.dto.ProfileResponseDto;
import project.landmark.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // 프로필 조회
    @GetMapping("/{id}")
    public ProfileResponseDto getProfile(@PathVariable Long id) {
        return profileService.getProfile(id);
    }

    // 칭호 변경
    @PutMapping("/{id}/title")
    public ResponseEntity<String> updateTitle(@PathVariable Long id, @RequestParam String newTitle) {
        profileService.updateTitle(id, newTitle);
        return ResponseEntity.ok("칭호가 변경되었습니다.");
    }
}

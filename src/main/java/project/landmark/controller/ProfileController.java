package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.landmark.dto.ProfileResponseDto;
import project.landmark.service.ProfileService;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    //프로필 조회
    @GetMapping
    public ProfileResponseDto getProfile(@RequestParam String username) {
        return profileService.getProfile(username);
    }

    //프로필 수정
    @PutMapping("/update")
    public ProfileResponseDto updateProfile(
            @RequestParam Long userId,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String title
    ) {
        return profileService.updateProfile(userId, nickname, title);
    }
}

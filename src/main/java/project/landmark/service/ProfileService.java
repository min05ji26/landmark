package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.landmark.dto.*;
import project.landmark.entity.*;
import project.landmark.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final TitleRepository titleRepository;

    // ✅ 1. 프로필 조회
    public ProfileResponseDto getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        // 친구 목록 변환
        List<FriendDto> friends = friendRepository.findByUser(user).stream()
                .map(f -> FriendDto.builder()
                        .id(f.getId())
                        .friendNickname(f.getFriend().getNickname())
                        .status(f.getStatus().name())
                        .build())
                .collect(Collectors.toList());

        // 획득한 칭호 목록 (예: Title 엔티티와 연결되어 있다면)
        List<String> titles = titleRepository.findAll().stream()
                .map(Title::getName)
                .collect(Collectors.toList());

        return ProfileResponseDto.builder()
                .nickname(user.getNickname())
                .totalSteps(user.getTotalSteps())
                .representativeTitle(user.getRepresentativeTitle())
                .friends(friends)
                .titles(titles)
                .build();
    }

    // ✅ 2. 프로필 수정 (닉네임 / 대표칭호)
    public ProfileResponseDto updateProfile(Long userId, String newNickname, String newTitle) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        if (newNickname != null) user.setNickname(newNickname);
        if (newTitle != null) user.setRepresentativeTitle(newTitle);

        userRepository.save(user);

        return getProfile(user.getUsername()); // 수정 후 최신 정보 반환
    }
}

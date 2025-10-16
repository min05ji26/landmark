package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.dto.FriendDto;
import project.landmark.dto.ProfileResponseDto;
import project.landmark.entity.Friend;
import project.landmark.entity.Title;
import project.landmark.entity.User;
import project.landmark.repository.FriendRepository;
import project.landmark.repository.TitleRepository;
import project.landmark.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final TitleRepository titleRepository;

    /* ✅ [1] 프로필 기본 정보 조회 */
    public ProfileResponseDto getProfileBasic(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return ProfileResponseDto.builder()

                .nickname(user.getNickname())
                .totalSteps(user.getTotalSteps())
                .representativeTitle(user.getRepresentativeTitle())
                .build();
    }

    /* ✅ [2] 친구 목록 조회 */
    public List<FriendDto> getMyFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Friend> friends = friendRepository.findByUser(user);

        return friends.stream()
                .map(f -> FriendDto.builder()
                        .id(f.getId())
                        .friendNickname(f.getFriend().getNickname())
                        .status(f.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }

    /* ✅ [3] 보유 칭호 목록 조회 */
    public List<String> getMyTitles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // (아직 보유 칭호 구조가 없으면 임시로 전체 목록 반환)
        // 나중에 UserTitle 테이블 연결 시 여기 수정
        return titleRepository.findAll().stream()
                .map(Title::getName)
                .collect(Collectors.toList());
    }

    /* ✅ [4] 대표 칭호 변경 */
    @Transactional
    public void updateTitle(Long userId, String newTitle) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Title title = titleRepository.findByTitleName(newTitle)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 칭호입니다."));

        user.setRepresentativeTitle(title.getName());
    }
    

}

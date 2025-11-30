package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.entity.Landmark;
import project.landmark.entity.User;
import project.landmark.repository.LandmarkRepository;
import project.landmark.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final LandmarkRepository landmarkRepository;

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableTitles(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        long mySteps = user.getTotalSteps() != null ? user.getTotalSteps() : 0L;
        List<Landmark> allLandmarks = landmarkRepository.findAll();

        List<String> titles = new ArrayList<>();

        for (Landmark lm : allLandmarks) {
            if (mySteps >= lm.getRequiredSteps() && lm.getRewardTitle() != null) {
                titles.add(lm.getRewardTitle());
            }
        }
        return titles;
    }

    public void updateRepresentativeTitle(String username, String newTitle) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        List<String> ownedTitles = getAvailableTitles(username);
        if (newTitle != null && !ownedTitles.contains(newTitle)) {
            throw new IllegalArgumentException("획득하지 못한 칭호입니다.");
        }

        user.setRepresentativeTitle(newTitle);
    }

    // 닉네임 변경
    public void updateNickname(String username, String newNickname) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        if (userRepository.findByNickname(newNickname).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        user.setNickname(newNickname);
    }

    // 프로필 이미지 변경
    public void updateProfileImage(String username, String imageUrl) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        user.setProfileImageUrl(imageUrl);
    }

    // 🚨 [추가] 상태 메시지 변경
    public void updateStatusMessage(String username, String newMessage) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        user.setStatusMessage(newMessage);
    }
}
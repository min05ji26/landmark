package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.entity.Achievement;
import project.landmark.entity.User;
import project.landmark.entity.UserAchievement;
import project.landmark.repository.AchievementRepository;
import project.landmark.repository.UserAchievementRepository;
import project.landmark.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final UserAchievementRepository userAchievementRepository;

    // 업적 등록 (Create)
    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    // 업적 전체 조회 (Read All)
    @Transactional(readOnly = true)
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    // 업적 단일 조회 (Read One)
    @Transactional(readOnly = true)
    public Optional<Achievement> getAchievement(Long id) {
        return achievementRepository.findById(id);
    }

    // 업적 수정 (Update)
    public Achievement updateAchievement(Long id, Achievement updated) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));

        achievement.setName(updated.getName());
        achievement.setDescription(updated.getDescription());
        achievement.setConditionType(updated.getConditionType());
        achievement.setConditionValue(updated.getConditionValue());
        achievement.setIconUrl(updated.getIconUrl());

        return achievementRepository.save(achievement);
    }

    // 업적 삭제 (Delete)
    public void deleteAchievement(Long id) {
        achievementRepository.deleteById(id);
    }

    // 업적 목록 조회 (정렬 및 필터 포함)
    // 업적 목록 조회 (정렬 및 필터 포함)
    @Transactional(readOnly = true)
    public List<Achievement> getAchievements(String sort, String conditionType) {
        List<Achievement> achievements;

        if (conditionType != null && !conditionType.isBlank()) {
            achievements = achievementRepository.findByConditionType(conditionType);
        } else {
            achievements = achievementRepository.findAll();
        }

        // 수정된 부분: 빼기(-) 대신 Long.compare 사용
        if ("desc".equalsIgnoreCase(sort)) {
            // 내림차순 (큰 게 먼저) -> b, a 순서
            achievements.sort((a, b) -> Long.compare(b.getConditionValue(), a.getConditionValue()));
        } else if ("asc".equalsIgnoreCase(sort)) {
            // 오름차순 (작은 게 먼저) -> a, b 순서
            achievements.sort((a, b) -> Long.compare(a.getConditionValue(), b.getConditionValue()));
        }

        return achievements;
    }

    // ✅ 조건 기반 업적 달성 (username 기준)
    public String unlockAchievements(String username, int currentSteps) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Achievement> achievements = achievementRepository.findByConditionType("step");

        int unlockedCount = 0;

        for (Achievement achievement : achievements) {
            if (currentSteps >= achievement.getConditionValue()) {
                boolean alreadyUnlocked = userAchievementRepository.existsByUserAndAchievement_Id(user, achievement.getId());
                if (!alreadyUnlocked) {
                    UserAchievement ua = UserAchievement.builder()
                            .user(user)
                            .achievement(achievement)
                            .achievedAt(LocalDateTime.now())
                            .build();
                    userAchievementRepository.save(ua);
                    unlockedCount++;
                }
            }
        }

        return "새로 달성된 업적 수: " + unlockedCount + "개";
    }

    // ✅ 사용자 업적 내역 조회 (username 기준)
    @Transactional(readOnly = true)
    public List<Achievement> getUserAchievements(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserAchievement> userAchievements = userAchievementRepository.findByUser(user);
        return userAchievements.stream()
                .map(UserAchievement::getAchievement)
                .collect(Collectors.toList());
    }
}

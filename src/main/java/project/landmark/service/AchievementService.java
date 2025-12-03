package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.dto.AchievementStatusDto;
import project.landmark.entity.*;
import project.landmark.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final NotificationRepository notificationRepository;

    private final StepRecordRepository stepRecordRepository;
    private final UserLandmarkRepository userLandmarkRepository;

    // 업적 생성
    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    // ✅ [수정] 전체 업적 + 내 달성 현황 조회 (조건 체크 로직 추가)
    @Transactional(readOnly = true)
    public List<AchievementStatusDto> getAchievementsWithStatus(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Achievement> allAchievements = achievementRepository.findAll();
        List<AchievementStatusDto> dtos = new ArrayList<>();

        for (Achievement ach : allAchievements) {
            // 1. 이미 해금(보상 획득) 했는지 확인
            boolean unlocked = userAchievementRepository.existsByUserAndAchievement_Id(user, ach.getId());

            // 2. 조건 달성 여부 확인 (해금 안됐을 때만 체크, 이미 해금했으면 true)
            boolean isMet = unlocked || checkCondition(user, ach);

            // 3. 조건 텍스트 생성
            String conditionText = generateConditionText(ach);

            dtos.add(AchievementStatusDto.builder()
                    .id(ach.getId())
                    .name(ach.getName())
                    .description(ach.getDescription())
                    .conditionType(ach.getConditionType())
                    .conditionValue(ach.getConditionValue())
                    .iconUrl(ach.getIconUrl())
                    .unlocked(unlocked)
                    .isConditionMet(isMet) // ✅ 서버에서 계산된 달성 여부 전달
                    .conditionText(conditionText) // ✅ 조건 설명 전달
                    .build());
        }

        // 정렬: 미달성 -> 달성(미획득) -> 획득완료 순서, 혹은 조건값 순서
        dtos.sort((a, b) -> Long.compare(a.getConditionValue(), b.getConditionValue()));

        return dtos;
    }

    // ✅ 조건 텍스트 생성 도우미
    private String generateConditionText(Achievement ach) {
        long val = ach.getConditionValue();
        switch (ach.getConditionType()) {
            case "total_steps":
                return "누적 " + String.format("%,d", val) + "보 달성 시";
            case "daily_steps_count":
                return "하루 20,000보 이상 " + val + "회 달성 시";
            case "consecutive_days":
                return val + "일 연속 5,000보 이상 달성 시";
            case "landmark_count":
                return "랜드마크 " + val + "곳 해금 시";
            case "early_morning_walks":
                return "오전 6~10시 8,000보 이상 " + val + "회 달성 시";
            default:
                return "조건 달성 시";
        }
    }

    // 업적 획득 (Claim)
    public String claimAchievement(String username, Long achievementId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Achievement achievement = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new IllegalArgumentException("Achievement not found"));

        if (userAchievementRepository.existsByUserAndAchievement_Id(user, achievementId)) {
            throw new IllegalArgumentException("이미 획득한 업적입니다.");
        }

        // 조건 재확인
        if (!checkCondition(user, achievement)) {
            throw new IllegalArgumentException("아직 조건을 달성하지 못했습니다.");
        }

        UserAchievement ua = UserAchievement.builder()
                .user(user)
                .achievement(achievement)
                .achievedAt(LocalDateTime.now())
                .build();
        userAchievementRepository.save(ua);

        Notification noti = Notification.builder()
                .user(user)
                .type(NotificationType.ACHIEVEMENT)
                .message(achievement.getName() + " 업적을 달성하셨습니다!")
                .isRead(false)
                .build();
        notificationRepository.save(noti);

        return achievement.getName();
    }

    // 조건 검사 로직
    private boolean checkCondition(User user, Achievement ach) {
        String type = ach.getConditionType();
        long targetValue = ach.getConditionValue();

        switch (type) {
            case "total_steps":
                long currentTotal = user.getTotalSteps() != null ? user.getTotalSteps() : 0L;
                return currentTotal >= targetValue;

            case "daily_steps_count": // 하루 2만보 N회
                long count20k = stepRecordRepository.findAllByUser(user).stream()
                        .filter(r -> r.getSteps() >= 20000)
                        .count();
                return count20k >= targetValue;

            case "consecutive_days": // 연속 N일 5천보
                List<StepRecord> records = stepRecordRepository.findAllByUserOrderByDateAsc(user);
                int maxStreak = 0;
                int currentStreak = 0;
                LocalDate prevDate = null;

                for (StepRecord r : records) {
                    if (r.getSteps() >= 5000) {
                        if (prevDate == null || r.getDate().isEqual(prevDate.plusDays(1))) {
                            currentStreak++;
                        } else if (!r.getDate().isEqual(prevDate)) {
                            currentStreak = 1;
                        }
                        maxStreak = Math.max(maxStreak, currentStreak);
                        prevDate = r.getDate();
                    } else {
                        currentStreak = 0;
                        prevDate = null;
                    }
                }
                return maxStreak >= targetValue;

            case "landmark_count":
                long unlockedCount = userLandmarkRepository.countByUser(user);
                return unlockedCount >= targetValue;

            case "early_morning_walks": // (임시) 하루 8천보 N회
                long count8k = stepRecordRepository.findAllByUser(user).stream()
                        .filter(r -> r.getSteps() >= 8000)
                        .count();
                return count8k >= targetValue;

            default:
                return false;
        }
    }

    // 기존 메서드들
    public Achievement updateAchievement(Long id, Achievement updated) {
        Achievement achievement = achievementRepository.findById(id).orElseThrow();
        achievement.setName(updated.getName());
        return achievementRepository.save(achievement);
    }

    public void deleteAchievement(Long id) {
        achievementRepository.deleteById(id);
    }

    public String unlockAchievements(String username, int currentSteps) {
        return "";
    }

    @Transactional(readOnly = true)
    public List<Achievement> getUserAchievements(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return userAchievementRepository.findByUser(user).stream()
                .map(UserAchievement::getAchievement)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Achievement> getAchievements(String sort, String type) {
        return achievementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Achievement> getAchievement(Long id) {
        return achievementRepository.findById(id);
    }
}
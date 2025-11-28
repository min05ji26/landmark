package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.entity.StepRecord;
import project.landmark.entity.User;
import project.landmark.repository.StepRecordRepository;
import project.landmark.repository.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class StepService {

    private final StepRecordRepository stepRecordRepository;
    private final UserRepository userRepository;
    private final AchievementService achievementService; // 업적 체크용

    public void addSteps(String username, int newSteps) {
        if (newSteps <= 0) return; // 0보 이하는 무시

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. 유저 총 걸음 수 증가
        // (User 엔티티에 있는 두 가지 필드 모두 업데이트)
        user.addSteps((long) newSteps); // 레벨 계산용 steps 업데이트

        // totalSteps가 null일 경우 대비
        long currentTotal = (user.getTotalSteps() == null) ? 0L : user.getTotalSteps();
        user.setTotalSteps(currentTotal + newSteps); // 랭킹용 totalSteps 업데이트

        // 2. 오늘 날짜 StepRecord 갱신 (없으면 생성, 있으면 합산)
        LocalDate today = LocalDate.now();
        StepRecord record = stepRecordRepository.findByUserAndDate(user, today)
                .orElseGet(() -> {
                    StepRecord newRecord = StepRecord.builder()
                            .user(user)
                            .date(today)
                            .steps(0L)
                            .build();
                    return stepRecordRepository.save(newRecord);
                });

        record.setSteps(record.getSteps() + newSteps);

        // (JPA 감지 기능으로 인해 save 호출 안 해도 트랜잭션 끝나면 자동 저장되지만, 명시적으로 호출해도 무방)
        stepRecordRepository.save(record);

        // 3. 업적 달성 체크 (기존 AchievementService 활용)
        achievementService.unlockAchievements(username, user.getTotalSteps().intValue());
    }
}
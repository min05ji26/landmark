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
    private final LandmarkService landmarkService;       // ✅ 랜드마크 체크용 추가

    public void addSteps(String username, int newSteps) {
        if (newSteps <= 0) return; // 0보 이하는 무시

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. 유저 총 걸음 수 증가
        user.addSteps((long) newSteps);

        long currentTotal = (user.getTotalSteps() == null) ? 0L : user.getTotalSteps();
        user.setTotalSteps(currentTotal + newSteps);

        // 2. 오늘 날짜 StepRecord 갱신
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
        stepRecordRepository.save(record);

        // 3. 업적 달성 체크 (알림 발송 포함)
        achievementService.unlockAchievements(username, user.getTotalSteps().intValue());

        // 4. ✅ 랜드마크 해금 체크 (알림 발송 포함)
        landmarkService.checkAndUnlockLandmarks(user);
    }
}
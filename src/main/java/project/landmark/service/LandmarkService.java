package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.dto.LandmarkProgressDto;
import project.landmark.entity.*;
import project.landmark.repository.LandmarkRepository;
import project.landmark.repository.NotificationRepository;
import project.landmark.repository.UserLandmarkRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LandmarkService {

    private final LandmarkRepository landmarkRepository;
    private final UserLandmarkRepository userLandmarkRepository;
    // ✅ 알림 저장을 위해 추가
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<Landmark> findAll() {
        return landmarkRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Landmark findById(Long id) {
        return landmarkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 랜드마크 ID입니다: " + id));
    }

    // 유저의 현재 랜드마크 이름을 계산해서 반환하는 메서드
    public String getCurrentLandmarkName(User user) {
        List<Landmark> allLandmarks = landmarkRepository.findAllByOrderByRequiredStepsAsc();

        String currentName = "시작점";
        long userSteps = user.getTotalSteps() != null ? user.getTotalSteps() : 0L;

        if (!allLandmarks.isEmpty()) {
            if (allLandmarks.get(0).getRequiredSteps() == 0) {
                currentName = allLandmarks.get(0).getName();
            } else {
                currentName = allLandmarks.get(0).getName() + " 입구";
            }
        }

        for (Landmark lm : allLandmarks) {
            if (userSteps >= lm.getRequiredSteps()) {
                currentName = lm.getName();
            } else {
                break;
            }
        }
        return currentName;
    }

    // ✅ [추가] 랜드마크 해금 체크 및 알림 발송 (StepService에서 호출)
    public void checkAndUnlockLandmarks(User user) {
        long currentSteps = user.getTotalSteps() != null ? user.getTotalSteps() : 0L;
        List<Landmark> allLandmarks = landmarkRepository.findAll();

        for (Landmark lm : allLandmarks) {
            // 걸음 수 조건 만족 시
            if (currentSteps >= lm.getRequiredSteps()) {
                // 아직 해금 기록이 없다면
                if (!userLandmarkRepository.existsByUserAndLandmark(user, lm)) {
                    // 1. 해금 기록 저장
                    UserLandmark unlocked = UserLandmark.builder()
                            .user(user)
                            .landmark(lm)
                            .unlockedAt(LocalDateTime.now())
                            .build();
                    userLandmarkRepository.save(unlocked);

                    // 2. 알림 생성 및 저장
                    Notification noti = Notification.builder()
                            .user(user)
                            .type(NotificationType.LANDMARK) // 새로 추가한 타입
                            .message(lm.getName() + " 랜드마크가 해금되었습니다!")
                            .isRead(false)
                            .build();
                    notificationRepository.save(noti);
                }
            }
        }
    }

    public List<LandmarkProgressDto> getLandmarksForUser(User user) {
        long userSteps = user.getTotalSteps() != null ? user.getTotalSteps() : 0L;
        List<Landmark> landmarks = landmarkRepository.findAll();

        return landmarks.stream()
                .map(landmark -> {
                    boolean isRecorded = userLandmarkRepository.existsByUserAndLandmark(user, landmark);
                    Long requiredSteps = landmark.getRequiredSteps();
                    if (requiredSteps == null) requiredSteps = 0L;

                    boolean unlocked = isRecorded || (userSteps >= requiredSteps) || (requiredSteps == 0);

                    int progressPercent;
                    if (requiredSteps == 0) {
                        progressPercent = 100;
                    } else {
                        progressPercent = (int) Math.round((double) userSteps * 100 / requiredSteps);
                        if (progressPercent > 100) progressPercent = 100;
                    }

                    return LandmarkProgressDto.builder()
                            .id(landmark.getId())
                            .name(landmark.getName())
                            .imageUrl(landmark.getImageUrl())
                            .requiredSteps(requiredSteps)
                            .currentSteps(userSteps)
                            .progressPercent(progressPercent)
                            .unlocked(unlocked)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
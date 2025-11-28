package project.landmark.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.entity.Achievement;
import project.landmark.entity.Landmark; // 👈 추가
import project.landmark.entity.StepRecord;
import project.landmark.entity.User;
import project.landmark.repository.AchievementRepository;
import project.landmark.repository.LandmarkRepository; // 👈 추가
import project.landmark.repository.StepRecordRepository;
import project.landmark.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final StepRecordRepository stepRecordRepository;
    private final LandmarkRepository landmarkRepository; // 👈 추가
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void initData() {
        // 1. 업적 데이터 초기화
        initAchievements();

        // 2. 랜드마크 데이터 초기화 (🚨 이 부분이 없어서 에러가 났었습니다)
        initLandmarks();

        // 3. 유저 및 걸음 수 데이터 초기화
        if (userRepository.count() == 0) {
            initMockUsersAndSteps();
        }
    }

    public void initAchievements() {
        List<String> names = List.of(
                "1000보 달성", "3000보 달성", "5000보 달성",
                "10000보 달성", "오늘의 걸음왕", "금주의 걸음왕",
                "랭킹 1등", "이달의 걸음왕", "운동 좀 해야겠는걸?", "걸음이 최고야"
        );

        for (String name : names) {
            if (achievementRepository.findByName(name) == null) {
                Achievement achievement = Achievement.builder()
                        .name(name)
                        .description(name + " 업적을 달성했습니다!")
                        .conditionType("step")
                        .conditionValue(1000L)
                        .rewardTitle(name)
                        .iconUrl("/images/achievements/default.png")
                        .build();
                achievementRepository.save(achievement);
            }
        }
    }

    // ✅ 랜드마크 데이터 생성 메서드
    public void initLandmarks() {
        if (landmarkRepository.count() == 0) {
            List<Landmark> landmarks = List.of(
                    Landmark.builder().name("우리집 앞 편의점").requiredSteps(1000L).description("가볍게 산책하기 좋아요").build(),
                    Landmark.builder().name("동네 공원").requiredSteps(3000L).description("신선한 공기를 마셔보세요").build(),
                    Landmark.builder().name("한강 공원").requiredSteps(5000L).description("강바람이 시원해요").build(),
                    Landmark.builder().name("남산 타워").requiredSteps(10000L).description("서울이 한눈에 보여요").build(),
                    Landmark.builder().name("한라산 백록담").requiredSteps(20000L).description("전설의 포켓몬이 살 것 같아요").build()
            );
            landmarkRepository.saveAll(landmarks);
        }
    }

    public void initMockUsersAndSteps() {
        // ... (기존 유저 생성 코드 유지)
        User user1 = createUser("user1", "장경준", "걷기의 신", 58000L, 5);
        User user2 = createUser("user2", "낑깡", "오사카성 정복자", 36000L, 3);
        User user3 = createUser("user3", "개발자", "초보 뚜벅이", 5000L, 1);

        userRepository.saveAll(List.of(user1, user2, user3));

        createStepRecords(user1, 8000L);
        createStepRecords(user2, 5000L);
        createStepRecords(user3, 1000L);
    }

    private User createUser(String username, String nickname, String title, Long totalSteps, int level) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode("1234"))
                .email(username + "@example.com")
                .nickname(nickname)
                .representativeTitle(title)
                .level(level)
                .totalSteps(totalSteps)
                .build();
    }

    private void createStepRecords(User user, Long dailySteps) {
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            StepRecord record = StepRecord.builder()
                    .user(user)
                    .date(today.minusDays(i))
                    .steps(dailySteps)
                    .build();
            stepRecordRepository.save(record);
        }
    }
}
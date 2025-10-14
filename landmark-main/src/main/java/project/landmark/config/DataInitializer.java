package project.landmark.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.landmark.entity.Achievement;
import project.landmark.repository.AchievementRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final AchievementRepository achievementRepository;

    @PostConstruct
    public void initAchievements() {
        List<String> names = List.of(
                "1000보 달성",
                "3000보 달성",
                "5000보 달성",
                "10000보 달성",
                "오늘의 걸음왕",
                "금주의 걸음왕",
                "랭킹 1등",
                "이달의 걸음왕",
                "운동 좀 해야겠는걸?",
                "걸음이 최고야"
        );

        for (String name : names) {
            if (achievementRepository.findByName(name) == null) {
                Achievement achievement = Achievement.builder()
                        .name(name)
                        .description(name + " 업적을 달성했습니다!")
                        .conditionType("step")
                        .conditionValue(0)
                        .iconUrl("/images/achievements/default.png")
                        .build();

                achievementRepository.save(achievement);
            }
        }
    }
}

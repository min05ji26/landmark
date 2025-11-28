package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.landmark.entity.Achievement;
import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Achievement findByName(String name);
    List<Achievement> findByConditionType(String conditionType);
}

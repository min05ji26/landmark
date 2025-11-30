package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.landmark.entity.Landmark;
import java.util.List;

public interface LandmarkRepository extends JpaRepository<Landmark, Long> {
    // ✅ 랜드마크를 걸음 수(requiredSteps)가 적은 순서대로 정렬해서 가져오는 메서드
    // 이 메서드가 없으면 RankingService에서 컴파일 에러가 발생합니다.
    List<Landmark> findAllByOrderByRequiredStepsAsc();
}
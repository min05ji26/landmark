package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.landmark.entity.Title;
import java.util.Optional;

public interface TitleRepository extends JpaRepository<Title, Long> {
    // 칭호 이름으로 DB에서 칭호 정보를 찾는 메서드
    Optional<Title> findByName(String name);
}
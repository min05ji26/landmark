package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.landmark.entity.Title;

public interface TitleRepository extends JpaRepository<Title, Long> {

    // 이름으로 칭호 찾기 (대표 칭호 변경 시 사용)
    Title findByName(String name);
}

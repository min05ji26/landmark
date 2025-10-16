package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.landmark.entity.Title;
import java.util.Optional;

public interface TitleRepository extends JpaRepository<Title, Long> {

    // 기존: Optional<Title> findByName(String name);
    Optional<Title> findByTitleName(String titleName); // ✅ 엔티티 필드명과 일치하게 변경
}

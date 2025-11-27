package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.landmark.entity.Landmark;

public interface LandmarkRepository extends JpaRepository<Landmark, Long> {
}

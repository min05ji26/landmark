package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.landmark.entity.Landmark;
import project.landmark.entity.User;
import project.landmark.entity.UserLandmark;

public interface UserLandmarkRepository extends JpaRepository<UserLandmark, Long> {
    boolean existsByUserAndLandmark(User user, Landmark landmark);
}


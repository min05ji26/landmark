package project.landmark.repository;

public interface UserLandmarkRepository extends JpaRepository<UserLandmark, Long> {
    boolean existsByUserAndLandmark(User user, Landmark landmark);
}


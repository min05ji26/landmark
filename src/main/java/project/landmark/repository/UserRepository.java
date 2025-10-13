package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.landmark.entity.User;

import java.util.Optional;

// 로그인한 사용자를 불러올 수 있게함
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

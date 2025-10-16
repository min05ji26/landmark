package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.landmark.entity.User;

import java.util.Optional;

// 로그인한 사용자를 불러올 수 있게함
public interface UserRepository extends JpaRepository<User, Long> {
    //우선 기본키 id로 조회할건데 혹시 모르니 남겨놓음 > 아이디로 조회하는 법
    Optional<User> findByUsername(String username);
}


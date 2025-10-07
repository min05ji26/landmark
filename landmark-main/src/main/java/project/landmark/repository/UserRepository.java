package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.landmark.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // username으로 사용자 찾기
    Optional<User> findByUsername(String username);

    // email로 사용자 찾기
    Optional<User> findByEmail(String email);

    // 닉네임으로 사용자 찾기
    Optional<User> findByNickname(String nickname);

    // username이 이미 존재하는지 여부
    boolean existsByUsername(String username);

    // email이 이미 존재하는지 여부
    boolean existsByEmail(String email);
}

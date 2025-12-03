package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.landmark.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    // 아이디 찾기용
    Optional<User> findByNicknameAndEmail(String nickname, String email);

    // ✅ [추가] 비밀번호 재설정 검증용 (아이디와 이메일이 일치하는지 확인)
    Optional<User> findByUsernameAndEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
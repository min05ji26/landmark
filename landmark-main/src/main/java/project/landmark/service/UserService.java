package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.entity.User;
import project.landmark.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // 회원가입
    public User registerUser(User user) {
        // 예시: 이메일 중복 체크
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        return userRepository.save(user);
    }

    // ID로 사용자 찾기
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // username으로 사용자 찾기
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

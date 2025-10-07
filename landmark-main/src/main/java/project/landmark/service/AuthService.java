package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // ✅ security 철자 주의
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.dto.RegisterRequest;
import project.landmark.dto.UserResponse;
import project.landmark.entity.User;
import project.landmark.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 해시
        String encoded = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .password(encoded)
                .email(request.getEmail())
                .nickname(request.getNickname())
                .totalSteps(0L)
                .representativeTitle(null)
                .build();

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .nickname(saved.getNickname())
                .totalSteps(saved.getTotalSteps())
                .representativeTitle(saved.getRepresentativeTitle())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}

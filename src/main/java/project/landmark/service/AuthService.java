package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.dto.LoginRequest;
import project.landmark.dto.RegisterRequest;
import project.landmark.dto.UserResponse;
import project.landmark.entity.User;
import project.landmark.repository.UserRepository;
import project.landmark.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

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

    // 로그인
    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.createToken(user.getId(), user.getUsername());
    }

    // 중복 확인
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameAvailable(String nickname) {
        return userRepository.findByNickname(nickname).isEmpty();
    }

    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    // 아이디 찾기
    @Transactional(readOnly = true)
    public String findUsername(String nickname, String email) {
        User user = userRepository.findByNicknameAndEmail(nickname, email)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자 정보를 찾을 수 없습니다."));
        return user.getUsername();
    }

    // 비밀번호 재설정 정보 검증
    @Transactional(readOnly = true)
    public void validateForResetPassword(String username, String email) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!user.getEmail().equals(email)) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }
    }

    // ✅ [수정] 비밀번호 재설정 (기존 비밀번호 확인 로직 추가)
    @Transactional
    public void resetPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 기존 비밀번호와 일치하는지 확인
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 이전과 일치합니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
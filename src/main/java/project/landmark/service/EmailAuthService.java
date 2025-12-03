package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.entity.User;
import project.landmark.repository.UserRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    // 인증번호 저장소 (메모리) : { "이메일": "인증번호" }
    // 실제 서비스에선 Redis 사용을 권장하지만, 간단한 구현을 위해 Map 사용
    private final Map<String, String> verificationStore = new ConcurrentHashMap<>();

    // 1. 인증번호 전송
    @Transactional(readOnly = true)
    public void sendVerificationCode(String nickname, String email) {
        // 유저 정보 확인
        User user = userRepository.findByNicknameAndEmail(nickname, email)
                .orElseThrow(() -> new IllegalArgumentException("입력하신 정보와 일치하는 회원이 없습니다."));

        // 인증번호 생성
        String code = emailService.createKey();

        // 저장소에 저장 (기존 코드가 있다면 덮어쓰기)
        verificationStore.put(email, code);

        // 이메일 발송
        emailService.sendEmail(email, "[Landmark] 아이디 찾기 인증번호",
                user.getNickname() + "님, 인증번호는 [" + code + "] 입니다.");
    }

    // 2. 인증번호 확인 및 아이디 반환
    @Transactional(readOnly = true)
    public String verifyCodeAndGetUsername(String email, String inputCode) {
        // 저장된 코드 확인
        String storedCode = verificationStore.get(email);

        if (storedCode == null) {
            throw new IllegalArgumentException("인증번호가 만료되었거나 요청되지 않았습니다.");
        }

        if (!storedCode.equals(inputCode)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        // 인증 성공 시 저장된 번호 삭제 (일회용)
        verificationStore.remove(email);

        // 아이디 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        return user.getUsername();
    }
}
package project.landmark;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import project.landmark.entity.*;
import project.landmark.repository.*;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StepRecordRepository stepRecordRepository;
    private final FriendRepository friendRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            if (userRepository.count() == 0) {
                // ✅ 유저 생성
                User u1 = userRepository.save(User.builder()
                        .username("kj77kj")
                        .password(passwordEncoder.encode("1234"))
                        .email("kj77@example.com")
                        .nickname("경준")
                        .district("강남구")
                        .build());

                User u2 = userRepository.save(User.builder()
                        .username("minji")
                        .password(passwordEncoder.encode("1234"))
                        .email("minji@example.com")
                        .nickname("민지")
                        .district("강남구")
                        .build());

                User u3 = userRepository.save(User.builder()
                        .username("chulsoo")
                        .password(passwordEncoder.encode("1234"))
                        .email("chulsoo@example.com")
                        .nickname("철수")
                        .district("송파구")
                        .build());

                // ✅ 친구 관계 (경준 → 민지, 철수)
                friendRepository.save(Friend.builder().user(u1).friend(u2).build());
                friendRepository.save(Friend.builder().user(u1).friend(u3).build());

                // ✅ 걸음 기록
                stepRecordRepository.saveAll(List.of(
                        StepRecord.builder().user(u1).date(LocalDate.now().minusDays(3)).steps(5400L).build(),
                        StepRecord.builder().user(u1).date(LocalDate.now().minusDays(1)).steps(6200L).build(),
                        StepRecord.builder().user(u2).date(LocalDate.now().minusDays(2)).steps(8800L).build(),
                        StepRecord.builder().user(u2).date(LocalDate.now()).steps(10200L).build(),
                        StepRecord.builder().user(u3).date(LocalDate.now().minusDays(5)).steps(3000L).build(),
                        StepRecord.builder().user(u3).date(LocalDate.now().minusDays(1)).steps(4500L).build()
                ));

                System.out.println("✅ 테스트용 유저, 친구, 걸음 데이터 삽입 완료!");
            } else {
                System.out.println("ℹ️ 이미 유저 데이터 존재 → 데이터 삽입 생략");
            }
        } catch (Exception e) {
            System.out.println("❌ TestDataRunner 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

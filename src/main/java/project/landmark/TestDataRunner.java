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
    private final PasswordEncoder passwordEncoder;
    private final AchievementRepository achievementRepository;

    @Override
    public void run(String... args) {
        try {
            // 1. 유저 데이터 초기화 (없을 때만 생성)
            if (userRepository.count() == 0) {
                // ✅ [요청사항] "경준" 유저 하나만 생성
                User u1 = userRepository.save(User.builder()
                        .username("kj77kj")
                        .password(passwordEncoder.encode("12345678"))
                        .email("kj77kj@naver.com")
                        .nickname("경준")
                        // district 등 필수값이 있다면 추가해야 함 (예: .district("서울"))
                        .build());

                System.out.println("✅ 테스트용 단일 유저('경준') 생성 완료!");
            } else {
                System.out.println("ℹ️ 이미 유저 데이터 존재 → 유저 생성 생략");
            }

            // 2. 업적 데이터 초기화 (데이터가 없을 때만 실행)
            if (achievementRepository.count() == 0) {
                List<Achievement> achievements = List.of(
                        Achievement.builder()
                                .name("럭키 세븐")
                                .description("난 살아있는 행운이라고?")
                                .conditionType("total_steps") // 누적 걸음
                                .conditionValue(77777L)
                                .iconUrl("lucky_seven") // ✅ 프론트엔드 매핑용 키값
                                .build(),
                        Achievement.builder()
                                .name("반짝이 신발")
                                .description("내 신발… 아직 살아있니?")
                                .conditionType("daily_steps_count") // 하루 2만보 3회
                                .conditionValue(3L)
                                .iconUrl("shiny_shoes")
                                .build(),
                        Achievement.builder()
                                .name("꾸준한 자")
                                .description("오늘도 한 발짝 더.")
                                .conditionType("consecutive_days") // 7일 연속 5천보
                                .conditionValue(7L)
                                .iconUrl("steady")
                                .build(),
                        Achievement.builder()
                                .name("걸음 수 수집가")
                                .description("발자국도 모으면 재산이지.")
                                .conditionType("total_steps")
                                .conditionValue(300000L)
                                .iconUrl("collector")
                                .build(),
                        Achievement.builder()
                                .name("마라톤 준비생")
                                .description("발걸음으로 마라톤 완주했다!")
                                .conditionType("total_steps")
                                .conditionValue(60000L)
                                .iconUrl("marathon")
                                .build(),
                        Achievement.builder()
                                .name("지구 둘레 한 바퀴(?)")
                                .description("지구 한 바퀴는 힘들지만… 시작은 해봤다.")
                                .conditionType("total_steps")
                                .conditionValue(500000L)
                                .iconUrl("earth")
                                .build(),
                        // "새벽 산책단" 제외됨
                        Achievement.builder()
                                .name("세계일주 기분내기")
                                .description("여긴 어디 또 어디?")
                                .conditionType("landmark_count") // 랜드마크 5곳 해금
                                .conditionValue(5L)
                                .iconUrl("world_tour")
                                .build(),
                        Achievement.builder()
                                .name("여행 중독자")
                                .description("한 번 걷기 시작하면 멈출 수 없지.")
                                .conditionType("total_steps")
                                .conditionValue(150000L)
                                .iconUrl("addict")
                                .build()
                );

                achievementRepository.saveAll(achievements);
                System.out.println("✅ 테스트용 업적 데이터 삽입 완료!");
            } else {
                System.out.println("ℹ️ 이미 업적 데이터 존재 → 업적 데이터 삽입 생략");
            }

        } catch (Exception e) {
            System.out.println("❌ TestDataRunner 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
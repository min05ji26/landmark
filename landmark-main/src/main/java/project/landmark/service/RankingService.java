package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.landmark.dto.UserRankingDto;
import project.landmark.entity.User;
import project.landmark.repository.FriendRepository;
import project.landmark.repository.StepRecordRepository;
import project.landmark.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final StepRecordRepository stepRecordRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    // ✅ 주간 랭킹
    public List<UserRankingDto> calculateWeeklyRanking() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);
        var result = stepRecordRepository.sumStepsByUserBetweenDates(start, end);
        return mapToRankingDto(result);
    }

    // ✅ 월간 랭킹
    public List<UserRankingDto> calculateMonthlyRanking() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.withDayOfMonth(1);
        var result = stepRecordRepository.sumStepsByUserBetweenDates(start, end);
        return mapToRankingDto(result);
    }

    // ✅ 구별 랭킹
    public List<UserRankingDto> calculateDistrictRanking(String district) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);
        var result = stepRecordRepository.sumStepsByDistrictBetweenDates(start, end, district);
        return mapToRankingDto(result);
    }

    // ✅ 친구 랭킹 (username 기준 개선)
    public List<UserRankingDto> calculateFriendRankingByUsername(String username) {
        // username으로 유저 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + username));

        // 친구 목록 조회
        List<User> friends = friendRepository.findFriendsByUser(user);

        // 나 자신 포함
        friends.add(user);

        // 최근 7일 기준
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);

        // 전체 걸음 데이터 중 친구 목록만 필터링
        var all = stepRecordRepository.sumStepsByUserBetweenDates(start, end);
        var filtered = all.stream()
                .filter(obj -> friends.contains((User) obj[0]))
                .collect(Collectors.toList());

        return mapToRankingDto(filtered);
    }

    // ✅ 공통 변환 메서드
    private List<UserRankingDto> mapToRankingDto(List<Object[]> result) {
        List<UserRankingDto> list = new ArrayList<>();
        long rank = 1;
        for (Object[] row : result) {
            User user = (User) row[0];
            Long totalSteps = (Long) row[1];
            list.add(new UserRankingDto(rank++, user.getNickname(), totalSteps));
        }
        return list;
    }
}

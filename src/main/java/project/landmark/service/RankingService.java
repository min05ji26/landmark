package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.dto.UserRankingDto;
import project.landmark.entity.Landmark;
import project.landmark.entity.User;
import project.landmark.repository.FriendRepository;
import project.landmark.repository.LandmarkRepository;
import project.landmark.repository.StepRecordRepository;
import project.landmark.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final StepRecordRepository stepRecordRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final LandmarkRepository landmarkRepository;

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

    // ✅ 친구 랭킹
    public List<UserRankingDto> calculateFriendRankingByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + username));

        List<User> friends = friendRepository.findFriendsByUser(user);
        friends.add(user);

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);

        var all = stepRecordRepository.sumStepsByUserBetweenDates(start, end);
        var filtered = all.stream()
                .filter(obj -> obj != null && obj.length >= 1 && friends.contains((User) obj[0]))
                .collect(Collectors.toList());

        return mapToRankingDto(filtered);
    }

    // ============================================================
    //                 🚨 Null-safe 공통 변환 메서드
    // ============================================================
    private List<UserRankingDto> mapToRankingDto(List<Object[]> result) {
        List<UserRankingDto> list = new ArrayList<>();
        int rank = 1;

        // 랜드마크 목록 null 안정화
        List<Landmark> landmarks = landmarkRepository.findAllByOrderByRequiredStepsAsc();
        if (landmarks == null) landmarks = new ArrayList<>();

        if (result == null) result = new ArrayList<>();

        for (Object[] row : result) {

            // 🔒 row null 방어
            if (row == null || row.length < 2) continue;

            User user = (User) row[0];
            Long totalSteps = (Long) row[1];

            // 🔒 user null 방어 → 유저가 없으면 스킵
            if (user == null) continue;

            // 🔒 totalSteps null 방어
            if (totalSteps == null) totalSteps = 0L;

            // 🔒 닉네임 null 방어
            String nickname = user.getNickname() != null ? user.getNickname() : "알 수 없음";

            // 🔒 대표칭호 null 허용
            String title = user.getRepresentativeTitle();

            // =======================
            //   현재 랜드마크 계산
            // =======================
            String currentLandmarkName = "집";
            long currentTotal = user.getTotalSteps() != null ? user.getTotalSteps() : 0L;

            for (Landmark lm : landmarks) {
                if (currentTotal >= lm.getRequiredSteps()) {
                    currentLandmarkName = lm.getName();
                } else break;
            }

            // DTO 생성
            list.add(new UserRankingDto(
                    user.getId(),
                    nickname,
                    totalSteps,
                    rank++,
                    title,
                    currentLandmarkName + " 여행 중..."
            ));
        }

        return list;
    }
}

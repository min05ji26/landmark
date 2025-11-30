package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
        try {
            // DB 에러가 나도 빈 리스트 반환 (409 에러 방지)
            var result = stepRecordRepository.sumStepsByUserBetweenDates(start, end);
            return mapToRankingDto(result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ 월간 랭킹
    public List<UserRankingDto> calculateMonthlyRanking() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.withDayOfMonth(1);
        try {
            var result = stepRecordRepository.sumStepsByUserBetweenDates(start, end);
            return mapToRankingDto(result);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ✅ 구별 랭킹
    public List<UserRankingDto> calculateDistrictRanking(String district) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);
        try {
            var result = stepRecordRepository.sumStepsByDistrictBetweenDates(start, end, district);
            return mapToRankingDto(result);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ✅ 친구 랭킹
    public List<UserRankingDto> calculateFriendRankingByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + username));

        List<User> friends = friendRepository.findFriendsByUser(user);
        friends.add(user);

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);

        try {
            var all = stepRecordRepository.sumStepsByUserBetweenDates(start, end);
            if (all == null) return new ArrayList<>();

            var filtered = all.stream()
                    .filter(obj -> obj != null && obj.length >= 1 && friends.contains((User) obj[0]))
                    .collect(Collectors.toList());

            return mapToRankingDto(filtered);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ============================================================
    //                 🚨 Null-safe 공통 변환 메서드
    // ============================================================
    private List<UserRankingDto> mapToRankingDto(List<Object[]> result) {
        List<UserRankingDto> list = new ArrayList<>();
        int rank = 1;

        // 🚨 안전하게 정렬 객체(Sort) 사용
        List<Landmark> landmarks = landmarkRepository.findAll(Sort.by(Sort.Direction.ASC, "requiredSteps"));
        if (landmarks == null) landmarks = new ArrayList<>();

        if (result == null) return list;

        for (Object[] row : result) {
            // 🔒 row 데이터 검증
            if (row == null || row.length < 2) continue;

            User user = (User) row[0];
            Long totalSteps = (Long) row[1];

            if (user == null) continue;
            if (totalSteps == null) totalSteps = 0L;

            String nickname = user.getNickname() != null ? user.getNickname() : "알 수 없음";
            String title = user.getRepresentativeTitle();

            // =======================
            //   현재 랜드마크 계산
            // =======================
            String currentLandmarkName = "시작점";
            if (!landmarks.isEmpty()) {
                currentLandmarkName = landmarks.get(0).getName() + " 입구";
            }

            long currentTotal = user.getTotalSteps() != null ? user.getTotalSteps() : 0L;

            for (Landmark lm : landmarks) {
                if (currentTotal >= lm.getRequiredSteps()) {
                    currentLandmarkName = lm.getName();
                } else break;
            }

            list.add(new UserRankingDto(
                    user.getId(),
                    nickname,
                    totalSteps,
                    rank++,
                    title,
                    currentLandmarkName
            ));
        }

        return list;
    }
}
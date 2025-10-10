package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.landmark.dto.UserRankingDto;
import project.landmark.repository.StepRecordRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final StepRecordRepository stepRecordRepository;

    /** 주간 랭킹 계산 */
    public List<UserRankingDto> calculateWeeklyRanking() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(WeekFields.ISO.getFirstDayOfWeek());
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<UserRankingDto> weeklySteps = stepRecordRepository.findStepsBetween(startOfWeek, endOfWeek);
        sortAndRank(weeklySteps);
        return weeklySteps;
    }

    /** 월간 랭킹 계산 */
    public List<UserRankingDto> calculateMonthlyRanking() {
        YearMonth thisMonth = YearMonth.now();
        LocalDate startOfMonth = thisMonth.atDay(1);
        LocalDate endOfMonth = thisMonth.atEndOfMonth();

        List<UserRankingDto> monthlySteps = stepRecordRepository.findStepsBetween(startOfMonth, endOfMonth);
        sortAndRank(monthlySteps);
        return monthlySteps;
    }

    /** 공통 정렬 및 순위 매기기 */
    private void sortAndRank(List<UserRankingDto> list) {
        list.sort((a, b) -> Long.compare(b.getTotalSteps(), a.getTotalSteps()));
        AtomicInteger rank = new AtomicInteger(1);
        list.forEach(r -> r.setRank(rank.getAndIncrement()));
    }
}

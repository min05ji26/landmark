package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.landmark.entity.StepRecord;
import project.landmark.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StepRecordRepository extends JpaRepository<StepRecord, Long> {

    // ✅ 추가: 유저와 날짜로 기록 찾기 (오늘 기록이 있는지 확인용)
    Optional<StepRecord> findByUserAndDate(User user, LocalDate date);

    // ✅ 전체 유저 걸음 수 합계 (기존 유지)
    @Query("SELECT sr.user, SUM(sr.steps) " +
            "FROM StepRecord sr " +
            "WHERE sr.date BETWEEN :start AND :end " +
            "GROUP BY sr.user " +
            "ORDER BY SUM(sr.steps) DESC")
    List<Object[]> sumStepsByUserBetweenDates(LocalDate start, LocalDate end);

    // ✅ 특정 구(district) 기준 유저 걸음 수 합계 (기존 유지)
    @Query("SELECT sr.user, SUM(sr.steps) " +
            "FROM StepRecord sr " +
            "WHERE sr.date BETWEEN :start AND :end " +
            "AND sr.user.district = :district " +
            "GROUP BY sr.user " +
            "ORDER BY SUM(sr.steps) DESC")
    List<Object[]> sumStepsByDistrictBetweenDates(LocalDate start, LocalDate end, String district);
}
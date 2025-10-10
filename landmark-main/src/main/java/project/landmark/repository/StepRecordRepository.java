package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.landmark.entity.StepRecord;
import project.landmark.dto.UserRankingDto;

import java.time.LocalDate;
import java.util.List;

public interface StepRecordRepository extends JpaRepository<StepRecord, Long> {

    /** 주간/월간 범위 걸음 수 합계 조회 */
    @Query("SELECT new project.landmark.dto.UserRankingDto(sr.user.id, sr.user.nickname, SUM(sr.steps)) " +
            "FROM StepRecord sr " +
            "WHERE sr.date BETWEEN :start AND :end " +
            "GROUP BY sr.user.id, sr.user.nickname")
    List<UserRankingDto> findStepsBetween(@Param("start") LocalDate start,
                                          @Param("end") LocalDate end);
}

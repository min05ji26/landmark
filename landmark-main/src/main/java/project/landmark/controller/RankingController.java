package project.landmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.landmark.dto.UserRankingDto;
import project.landmark.service.RankingService;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    /**
     * 주간 랭킹 조회 API
     * GET /api/ranking/weekly
     */
    @GetMapping("/weekly")
    public ResponseEntity<?> getWeeklyRanking() {
        List<UserRankingDto> ranking = rankingService.calculateWeeklyRanking();
        return ResponseEntity.ok().body(ranking);
    }

    /**
     * 월간 랭킹 조회 API
     * GET /api/ranking/monthly
     */
    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyRanking() {
        List<UserRankingDto> ranking = rankingService.calculateMonthlyRanking();
        return ResponseEntity.ok().body(ranking);
    }
}

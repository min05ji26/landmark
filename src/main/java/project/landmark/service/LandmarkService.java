package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.dto.LandmarkProgressDto;
import project.landmark.entity.Landmark;
import project.landmark.entity.User;
import project.landmark.repository.LandmarkRepository;
import project.landmark.repository.UserLandmarkRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LandmarkService {

    private final LandmarkRepository landmarkRepository;
    private final UserLandmarkRepository userLandmarkRepository;

    // 전체 랜드마크 목록 조회 > 디비에서 가져옴
    @Transactional(readOnly = true)
    public List<Landmark> findAll() {
        return landmarkRepository.findAll();
    }

    // 아이디로 랜드마크 찾기 (랜드마크 단건조회)
    @Transactional(readOnly = true)
    public Landmark findById(Long id) {
        return landmarkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 랜드마크 ID입니다: " + id));
    }

    // [핵심 메서드] 유저 기준 랜드마크 리스트 + 진행률/해금 여부 조회
    /**
     *      1) 이미 해금됐는지(UserLandmark 존재 여부) 확인
     *      2) 유저 걸음 수와 requiredSteps로 진행률(%) 계산 (최대 100%)
     *      3) 위 정보를 LandmarkProgressDto로 변환해서 리스트로 반환
     */
    public List<LandmarkProgressDto> getLandmarksForUser(User user) {

        // 1) 유저의 현재 걸음 수 (로그인에서 이미 유저는 보장된 상태라고 가정)
        long userSteps = user.getSteps() != null ? user.getSteps() : 0L;

        // 2) DB에서 모든 랜드마크 조회 (정렬 필요하면 여기서 추가)
        List<Landmark> landmarks = landmarkRepository.findAll();

        // 3) 각 랜드마크 → DTO로 변환
        return landmarks.stream()
                .map(landmark -> {

                    // 3-1) 이 유저가 이 랜드마크를 해금했는지 여부
                    boolean unlocked = userLandmarkRepository.existsByUserAndLandmark(user, landmark);

                    Long requiredSteps = landmark.getRequiredSteps();
                    if (requiredSteps == null || requiredSteps <= 0) {
                        requiredSteps = 1L; // 0 나누기 방지용 안전 처리
                    }

                    // 3-2) 진행률 계산 (예: 12000 / 5000 * 100 = 240% → 100으로 고정)
                    int progressPercent = (int) Math.round((double) userSteps * 100 / requiredSteps);
                    if (progressPercent > 100) {
                        progressPercent = 100;
                    }

                    // 3-3) DTO로 묶어서 반환
                    return LandmarkProgressDto.builder()
                            .id(landmark.getId())
                            .name(landmark.getName())
                            .imageUrl(landmark.getImageUrl())   // 엔티티에 imageUrl 필드 있다고 가정
                            .requiredSteps(landmark.getRequiredSteps())
                            .currentSteps(userSteps)            // 🔹 실제 유저 걸음 수 그대로 (12000 등)
                            .progressPercent(progressPercent)   // 🔹 바에는 이 값 사용 (최대 100)
                            .unlocked(unlocked)
                            .build();
                })
                .collect(Collectors.toList());
    }

}

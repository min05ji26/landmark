package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.landmark.entity.Landmark;
import project.landmark.repository.LandmarkRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LandmarkService {

    private final LandmarkRepository landmarkRepository;

    //Landmark 객체 만들면서 랜드마크 데이터 넣기
    //이후에 수정하기!!!!!!!!!!
    private final List<Landmark> landmarks = List.of(
            new Landmark(1L, "해운대", 20000L, "부산 해운대 해변", "해운대 빠돌이"),
            new Landmark(2L, "오사카성", 50000L, "일본 오사카성", "오사카 정복자"),
            new Landmark(3L, "에펠탑", 100000L, "프랑스 파리 에펠탑", "파리 정복자")
    );

    //생성
    public Landmark save(Landmark landmark){
        return landmarkRepository.save(landmark);
    }

    //전체 조회
    public List<Landmark> findAll(){
        return landmarkRepository.findAll();
    }

    //랜드마크 단건 조회
    public Landmark findById(Long id) {
        return landmarks.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 랜드마크 없음: " + id));
    }

    //수정
    public Landmark update(Long id, Landmark updatedLandmark){
        return landmarkRepository.findById(id)
                .map(l -> {
                    l.setName(updatedLandmark.getName());
                    l.setRequiredSteps(updatedLandmark.getRequiredSteps());
                    l.setDescription(updatedLandmark.getDescription());
                    l.setRewardTitle(updatedLandmark.getRewardTitle());
                    return landmarkRepository.save(l);

        })
                .orElseThrow(() -> new IllegalArgumentException("해당 랜드마크 없음 : " + id));
    }

    //삭제
    public void delete(Long id){
        landmarkRepository.deleteById(id);
    }
}

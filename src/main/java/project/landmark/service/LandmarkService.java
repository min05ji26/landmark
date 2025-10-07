package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.landmark.entity.Landmark;
import project.landmark.repository.LandmarkRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LandmarkService {

    //private final LandmarkRepository landmarkRepository;

    //Landmark 객체 만들면서 랜드마크 데이터 넣기
    private final List<Landmark> landmarks = List.of(
            new Landmark(1L, "해운대", 20000L, "부산 해운대 해변", "해운대 빠돌이"),
            new Landmark(2L, "오사카성", 50000L, "일본 오사카성", "오사카 정복자"),
            new Landmark(3L, "에펠탑", 100000L, "프랑스 파리 에펠탑", "파리 정복자"),
            new Landmark(4L, "자유의 여신상", 150000L, "미국 뉴욕 자유의 여신상", "자유의 수호자"),
            new Landmark(5L, "만리장성", 200000L, "중국 만리장성", "장성 정복자"),
            new Landmark(6L, "타지마할", 250000L, "인도 아그라 타지마할", "인도의 보석"),
            new Landmark(7L, "피라미드", 300000L, "이집트 기자 피라미드", "파라오의 후예"),
            new Landmark(8L, "오페라하우스", 350000L, "호주 시드니 오페라하우스", "남반구의 예술가")
    );


    //생성
    //public Landmark save(Landmark landmark){
    //    return landmarkRepository.save(landmark);
    //}

    public List<Landmark> findAll(){
        return landmarks;
    }

    //랜드마크 단건 조회
    public Landmark findById(Long id) {
        return landmarks.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 랜드마크 없음: " + id));
    }

}

package project.landmark.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 업적명
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // 업적 설명 (kj 기능)
    @Column(nullable = false, length = 200)
    private String description;

    // 조건 타입 (두 브랜치 공통)
    @Column(nullable = false)
    private String conditionType;

    // 조건 값 (Long으로 통일)
    @Column(nullable = false)
    private Long conditionValue;

    // 업적 달성 시 보상 칭호 (mj 기능)
    private String rewardTitle;

    // 아이콘 이미지 경로 (kj 기능)
    private String iconUrl;
}

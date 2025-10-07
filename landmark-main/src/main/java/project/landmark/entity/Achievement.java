package project.landmark.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Achievement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;            // 예: 럭키세븐
    private String conditionType;   // 총 걸음 수, 특정 패턴 등
    private Long conditionValue;    // 예: 7777
    private String rewardTitle;     // 달성 시 칭호
}
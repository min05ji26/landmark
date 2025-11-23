package project.landmark.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Landmark {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;            // 예: 해운대
    private Long requiredSteps;     // 필요 걸음 수
    private String description;
    private String rewardTitle;     // 해금 시 얻는 칭호
    private String imageUrl;
}

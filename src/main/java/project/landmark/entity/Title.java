package project.landmark.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Title {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이름 통일
    @Column(nullable = false)
    private String name;

    private String description;

    // mj 기능 추가 (있을 수도 있는 조건)
    private Long requiredSteps;
}

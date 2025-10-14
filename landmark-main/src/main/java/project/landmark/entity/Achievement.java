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

    @Column(nullable = false, unique = true, length = 50)
    private String name; // 업적명 (예: '10000보 달성')

    @Column(nullable = false, length = 200)
    private String description; // 업적 설명

    @Column(nullable = false)
    private int conditionValue; // 조건 값 (예: 10000보)

    @Column(nullable = false)
    private String conditionType; // 조건 타입 (예: "step", "landmark")

    private String iconUrl; // 업적 아이콘 이미지 경로
}

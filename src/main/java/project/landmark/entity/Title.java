package project.landmark.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
// 존재 가능한 모든 칭호를 정의하는 테이블
public class Title {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long titleId;

    private String titleName;
    //조건
    private String description;
    private Long requiredSteps;

    public String getName() {
        return titleName;
    }
}

package project.landmark.entity;

import jakarta.persistence.*;
import lombok.*;
import project.landmark.entity.Title;
import project.landmark.entity.User;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserTitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 외래키 컬럼 이름은 실제 DB와 동일하게 'user_id'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "title_id")
    private Title title;

    private boolean isRepresentative; // 대표 칭호 여부
}

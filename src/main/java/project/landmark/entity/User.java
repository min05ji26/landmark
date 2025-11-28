package project.landmark.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ----- kj 기능 (인증/회원가입) -----
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 30)
    private String district;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ----- 공통 필드 -----
    private Long totalSteps = 0L;
    private String representativeTitle;

    // ----- mj 기능 (레벨링 시스템) -----
    private Long steps = 0L;
    private int level = 1;
    private String title; // mj title

    public void addSteps(Long newSteps) {
        if (this.steps == null) this.steps = 0L;
        this.steps += newSteps;
        checkLevelUp();
    }

    private void checkLevelUp() {
        int newLevel = (int)(steps / 10000) + 1;
        if (newLevel > this.level) {
            this.level = newLevel;
        }
    }

    public void changeTitle(String newTitle) {
        this.title = newTitle;
    }
}

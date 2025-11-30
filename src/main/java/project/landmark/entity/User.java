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

    @Builder.Default
    private Long totalSteps = 0L;

    private String representativeTitle;

    @Builder.Default
    private int level = 1;

    @Column(columnDefinition = "LONGTEXT")
    private String profileImageUrl;

    // 🚨 [추가] 상태 메시지 컬럼
    private String statusMessage;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.totalSteps == null) this.totalSteps = 0L;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addSteps(Long newSteps) {
        if (this.totalSteps == null) this.totalSteps = 0L;
        this.totalSteps += newSteps;
        checkLevelUp();
    }

    private void checkLevelUp() {
        int newLevel = (int)(this.totalSteps / 10000) + 1;
        if (newLevel > this.level) {
            this.level = newLevel;
        }
    }
}
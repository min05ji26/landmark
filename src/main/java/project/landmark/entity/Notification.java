package project.landmark.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 알림을 받는 사람

    // 알림 타입
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    private String senderName;

    // ✅ [수정] 이미지 데이터가 길 수 있으므로 LONGTEXT로 변경
    @Column(columnDefinition = "LONGTEXT")
    private String senderProfileImage;

    private boolean isRead;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
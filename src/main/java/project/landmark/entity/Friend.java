package project.landmark.entity;

import jakarta.persistence.*;
import lombok.*;
import project.landmark.entity.FriendStatus;
import project.landmark.entity.User;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Friend {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    private User friend;

    @Enumerated(EnumType.STRING)
    private FriendStatus status; // REQUESTED, ACCEPTED, BLOCKED
}

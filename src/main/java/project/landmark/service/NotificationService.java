package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.entity.Notification;
import project.landmark.entity.NotificationType;
import project.landmark.entity.User;
import project.landmark.repository.NotificationRepository;
import project.landmark.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FriendService friendService;

    // 내 알림 목록 (페이징 적용)
    public Slice<Notification> getMyNotifications(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        PageRequest pageRequest = PageRequest.of(page, size);
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageRequest);
    }

    // ✅ [추가] 안 읽은 알림 개수 조회
    @Transactional(readOnly = true)
    public long getUnreadCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    // 알림 삭제
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    // 친구 수락
    public void acceptFriendRequest(Long notificationId, String myUsername) {
        Notification noti = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림 없음"));

        if (noti.getSenderName() != null) {
            try {
                friendService.addFriend(myUsername, noti.getSenderName());
            } catch (Exception e) {
                System.out.println("친구 추가 실패: " + e.getMessage());
            }
        }

        // 처리된 알림 삭제
        notificationRepository.delete(noti);
    }

    // 친구 거절
    public void rejectFriendRequest(Long notificationId, String myUsername) {
        Notification noti = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림 없음"));

        User sender = userRepository.findByNickname(noti.getSenderName()).orElse(null);
        if (sender != null) {
            User me = userRepository.findByUsername(myUsername).orElseThrow();
            Notification rejectNoti = Notification.builder()
                    .user(sender)
                    .type(NotificationType.FRIEND_REJECT)
                    .message(me.getNickname() + "님이 친구 요청을 거절하셨습니다.")
                    .isRead(false)
                    .build();
            notificationRepository.save(rejectNoti);
        }

        notificationRepository.delete(noti);
    }
}
package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.dto.FriendDto;
import project.landmark.entity.*;
import project.landmark.repository.FriendRepository;
import project.landmark.repository.NotificationRepository;
import project.landmark.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final LandmarkService landmarkService;

    // 알림 저장을 위해 Repository 주입
    private final NotificationRepository notificationRepository;

    // 친구 목록 조회
    @Transactional(readOnly = true)
    public List<FriendDto> getMyFriends(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // ACCEPTED 상태인 친구만 조회
        return friendRepository.findByUser(user).stream()
                .filter(f -> f.getStatus() == FriendStatus.ACCEPTED)
                .map(f -> {
                    User friendUser = f.getFriend();
                    String currentLandmark = landmarkService.getCurrentLandmarkName(friendUser);

                    return FriendDto.builder()
                            .id(f.getId())
                            .friendNickname(friendUser.getNickname())
                            .status(f.getStatus().name())
                            .currentLandmark(currentLandmark)
                            .totalSteps(friendUser.getTotalSteps())
                            .profileImageUrl(friendUser.getProfileImageUrl())
                            .representativeTitle(friendUser.getRepresentativeTitle())
                            .statusMessage(friendUser.getStatusMessage())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 친구 요청 보내기
    public void requestFriend(String myUsername, String friendNickname) {
        User me = userRepository.findByUsername(myUsername)
                .orElseThrow(() -> new IllegalArgumentException("내 정보 없음"));

        User friendUser = userRepository.findByNickname(friendNickname)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (me.getId().equals(friendUser.getId())) {
            throw new IllegalArgumentException("자기 자신에게 요청할 수 없습니다.");
        }

        boolean isAlreadyFriend = friendRepository.findByUserAndFriend(me, friendUser)
                .map(f -> f.getStatus() == FriendStatus.ACCEPTED)
                .orElse(false);

        if (isAlreadyFriend) {
            throw new IllegalArgumentException("이미 친구입니다.");
        }

        // 상대방에게 '친구 요청' 알림 생성
        Notification noti = Notification.builder()
                .user(friendUser)
                .type(NotificationType.FRIEND_REQUEST)
                .message(me.getNickname() + "님이 친구 요청을 보냈습니다.")
                .senderName(me.getNickname())
                .senderProfileImage(me.getProfileImageUrl())
                .isRead(false)
                .build();

        notificationRepository.save(noti);
    }

    // ✅ [수정] 친구 수락 (양방향 저장 + 수락 알림 발송)
    public void addFriend(String myUsername, String friendNickname) {
        User me = userRepository.findByUsername(myUsername)
                .orElseThrow(() -> new IllegalArgumentException("내 정보 없음"));

        User friendUser = userRepository.findByNickname(friendNickname)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 닉네임입니다."));

        // 1. 나 -> 친구 (수락 상태로 저장/업데이트)
        Friend friendship1 = friendRepository.findByUserAndFriend(me, friendUser)
                .orElse(Friend.builder().user(me).friend(friendUser).build());
        friendship1.setStatus(FriendStatus.ACCEPTED);
        friendRepository.save(friendship1);

        // 2. 친구 -> 나 (수락 상태로 저장/업데이트)
        Friend friendship2 = friendRepository.findByUserAndFriend(friendUser, me)
                .orElse(Friend.builder().user(friendUser).friend(me).build());
        friendship2.setStatus(FriendStatus.ACCEPTED);
        friendRepository.save(friendship2);

        // ✅ [추가] 요청자(friendUser)에게 '수락 완료' 알림 보내기
        Notification acceptNoti = Notification.builder()
                .user(friendUser) // 알림 받을 사람 (요청했던 사람)
                .type(NotificationType.FRIEND_ACCEPT) // 수락 알림 타입
                .message(me.getNickname() + "님이 친구 요청을 수락하셨습니다!")
                .senderName(me.getNickname()) // 수락한 사람(나)의 이름
                .senderProfileImage(me.getProfileImageUrl()) // 수락한 사람(나)의 프사
                .isRead(false)
                .build();

        notificationRepository.save(acceptNoti);
    }

    // 친구 삭제
    public void deleteFriend(String myUsername, String friendNickname) {
        User me = userRepository.findByUsername(myUsername)
                .orElseThrow(() -> new IllegalArgumentException("내 정보 없음"));
        User friendUser = userRepository.findByNickname(friendNickname)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구입니다."));

        // 양쪽 관계를 모두 삭제
        friendRepository.findByUserAndFriend(me, friendUser).ifPresent(friendRepository::delete);
        friendRepository.findByUserAndFriend(friendUser, me).ifPresent(friendRepository::delete);

        // ✅ [추가] 삭제된 친구에게 절교 알림 발송
        Notification rejectNoti = Notification.builder()
                .user(friendUser) // 알림 받을 사람 (삭제된 친구)
                .type(NotificationType.FRIEND_REJECT) // 거절/절교 타입 사용
                .message(me.getNickname() + "님과 절교하셨습니다.") // 요청하신 문구
                .senderName(me.getNickname())
                .senderProfileImage(me.getProfileImageUrl())
                .isRead(false)
                .build();
        notificationRepository.save(rejectNoti);
    }
}
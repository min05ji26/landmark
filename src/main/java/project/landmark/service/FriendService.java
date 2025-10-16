package project.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.landmark.entity.Friend;
import project.landmark.entity.FriendStatus;
import project.landmark.entity.User;
import project.landmark.repository.FriendRepository;
import project.landmark.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    // ✅ 친구 요청 보내기
    public void sendFriendRequest(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("요청 보낸 사용자를 찾을 수 없습니다."));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new IllegalArgumentException("요청 받은 사용자를 찾을 수 없습니다."));

        // 중복 요청 방지
        boolean exists = friendRepository.findByUser(fromUser).stream()
                .anyMatch(f -> f.getFriend().equals(toUser));
        if (exists) {
            throw new IllegalStateException("이미 친구 요청을 보냈거나 친구입니다.");
        }

        Friend friend = Friend.builder()
                .user(fromUser)
                .friend(toUser)
                .status(FriendStatus.REQUESTED)
                .build();

        friendRepository.save(friend);
    }

    // ✅ 친구 요청 수락
    public void acceptFriendRequest(Long requestId) {
        Friend friendRequest = friendRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));

        friendRequest.setStatus(FriendStatus.ACCEPTED);

        // 양방향 친구 관계 생성
        Friend reverseRelation = Friend.builder()
                .user(friendRequest.getFriend())
                .friend(friendRequest.getUser())
                .status(FriendStatus.ACCEPTED)
                .build();

        friendRepository.save(reverseRelation);
        friendRepository.save(friendRequest);
    }

    // ✅ 친구 삭제
    public void deleteFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("친구를 찾을 수 없습니다."));

        List<Friend> relations = friendRepository.findByUser(user);
        List<Friend> reverseRelations = friendRepository.findByUser(friend);

        relations.stream()
                .filter(f -> f.getFriend().equals(friend))
                .forEach(friendRepository::delete);
        reverseRelations.stream()
                .filter(f -> f.getFriend().equals(user))
                .forEach(friendRepository::delete);
    }
}

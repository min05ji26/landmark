package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.landmark.entity.Friend;
import project.landmark.entity.User;
import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    // 유저의 친구 목록 조회
    List<Friend> findByUser(User user);

    // 양방향 친구 관계 (상대방 입장에서도 나를 친구로 등록했을 때)
    List<Friend> findByFriend(User friend);
}

package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.landmark.entity.Friend;
import project.landmark.entity.User;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    // 🔹 kj 기능: User 엔티티로 친구 가져오기
    @Query("SELECT f.friend FROM Friend f WHERE f.user = :user")
    List<User> findFriendsByUser(User user);

    // 🔹 mj 기능: Friend 엔티티 기반 친구 관계 조회
    List<Friend> findByUser(User user);

    List<Friend> findByFriend(User friend);
}

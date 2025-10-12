package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.landmark.entity.Friend;
import project.landmark.entity.User;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    // ✅ 특정 유저의 친구 목록 조회
    @Query("SELECT f.friend FROM Friend f WHERE f.user = :user")
    List<User> findFriendsByUser(User user);
}

package project.landmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.landmark.entity.Friend;
import project.landmark.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    // ✅ [복구] RankingService에서 사용하는 메서드 (User 리스트 반환)
    @Query("SELECT f.friend FROM Friend f WHERE f.user = :user")
    List<User> findFriendsByUser(@Param("user") User user);

    // ---------------------------------------------------------

    // 내 친구 목록 조회 (Friend 엔티티 반환)
    List<Friend> findByUser(User user);

    // 이미 친구인지 확인 (중복 추가 방지)
    boolean existsByUserAndFriend(User user, User friend);

    // 친구 관계 찾기 (삭제용)
    Optional<Friend> findByUserAndFriend(User user, User friend);
}
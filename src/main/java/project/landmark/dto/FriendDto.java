package project.landmark.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class FriendDto {
    //비밀번호 등 민감한 정보는 제외하고 프로필 상에서 보이는 친구페이지
    private Long id;                // 친구 관계 ID
    private String friendNickname;  // 친구의 닉네임
    private String status;          // 친구 상태 (요청 수락 등)
}

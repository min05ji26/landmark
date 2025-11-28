package project.landmark.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TitleDto {
    // 칭호 관련 페이지
    private String name;          // 칭호명
    private String description;   // 칭호 설명
}
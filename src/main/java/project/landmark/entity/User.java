@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;   // 유저명
    private Long steps;        // 누적 걸음 수
    private int level;         // 레벨
    private String title;      // 대표 칭호 (ex. "해운대 빠돌이")

    // 걸음 수 추가 시 자동 레벨업
    public void addSteps(Long newSteps) {
        this.steps += newSteps;
        checkLevelUp();
    }

    private void checkLevelUp() {
        int newLevel = (int)(steps / 10000) + 1; // 예시: 1만보마다 레벨 1씩 상승
        if (newLevel > this.level) {
            this.level = newLevel;
        }
    }

    public void changeTitle(String newTitle) {
        this.title = newTitle;
    }
}

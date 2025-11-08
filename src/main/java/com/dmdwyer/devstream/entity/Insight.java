import java.time.LocalDateTime;

@Entity
public class Insight {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String topic;
    private String detail;
    private LocalDateTime createdAt = LocalDateTime.now();
}
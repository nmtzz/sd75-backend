package gadgetify.server.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "forget_password")
public class ForgetPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 10)
    @Nationalized
    @Column(name = "pin", length = 10)
    private String pin;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @ColumnDefault("0")
    @Column(name = "attempt_count")
    private Integer attemptCount;

    @Column(name = "is_verified")
    private Boolean isVerified;

}

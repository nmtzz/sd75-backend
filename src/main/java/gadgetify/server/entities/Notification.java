package gadgetify.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Size(max = 255)
    @Nationalized
    @Column(name = "content")
    private String content;

    @Size(max = 255)
    @Nationalized
    @Column(name = "link")
    private String link;

    @ColumnDefault("0")
    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "created_at")
    private Instant createdAt;

}

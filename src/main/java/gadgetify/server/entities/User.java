package gadgetify.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "email")
    @NotNull(message = "Email is required")
    private String email;

    @Size(max = 255)
    @Nationalized
    @Column(name = "password")
    private String password;

    @Size(max = 15)
    @Nationalized
    @ColumnDefault("0")
    @Column(name = "role", length = 15)
    private String role;

    @ColumnDefault("1")
    @Column(name = "status")
    private Boolean status;


    @Size(max = 50)
    @Nationalized
    @Column(name = "full_name", length = 50)
    @NotNull(message = "Full name is required")
    private String fullName;

    @Transient
    private String token;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Rating> ratings = new LinkedHashSet<>();

    @Size(max = 255)
    @Nationalized
    @Column(name = "picture_url")
    private String pictureUrl;

    @Size(max = 30)
    @Nationalized
    @Column(name = "provider", length = 30)
    private String provider;

    public User toBasicResponse() {
        this.setPassword(null);
        this.setRatings(null);
        return this;
    }

}
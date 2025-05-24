package gadgetify.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "promotion_translations")
public class PromotionTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    @JsonIgnore
    private Promotion promotion;

    @Size(max = 255)
    @Nationalized
    @Column(name = "translated_name")
    private String translatedName;

    @Size(max = 30)
    @Nationalized
    @Column(name = "\"language\"", length = 30)
    private String language;

}
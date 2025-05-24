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
@Table(name = "product_translations")
public class ProductTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @Size(max = 255)
    @Nationalized
    @Column(name = "translated_name")
    private String translatedName;

    @Size(max = 400)
    @Nationalized
    @Column(name = "translated_description", length = 400)
    private String translatedDescription;

    @Nationalized
    @Lob
    @Column(name = "translated_specs")
    private String translatedSpecs;

    @Size(max = 30)
    @Nationalized
    @Column(name = "\"language\"", length = 30)
    private String language;

}
package gadgetify.server.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "name")
    private String name;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Transient
    private Integer productCount;

    @Transient
    private Set<Integer> productIds;


    @OneToMany(mappedBy = "promotion", fetch = FetchType.EAGER)
    private Set<PromotionDetail> promotionDetails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "promotion", fetch = FetchType.EAGER)
    private Set<PromotionTranslation> promotionTranslations = new LinkedHashSet<>();


    @PostLoad
    public void handle() {
        if (this.promotionDetails != null) {
            this.productCount = this.promotionDetails.size();
        }
        String locale = LocaleContextHolder.getLocale().getLanguage();
        if (this.promotionTranslations != null && !locale.equalsIgnoreCase("vi")) {
            var localizedName = promotionTranslations.stream()
                    .filter(promotionTranslation -> promotionTranslation.getLanguage().equals(locale))
                    .findFirst()
                    .map(PromotionTranslation::getTranslatedName)
                    .orElse(name);
            this.setName(localizedName);
        }
    }

    public Promotion toBasicResponse() {
        this.setPromotionDetails(null);
        this.setPromotionTranslations(null);
        return this;
    }

}
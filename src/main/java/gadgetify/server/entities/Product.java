package gadgetify.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Size(max = 255)
    @Nationalized
    @Column(name = "thumbnail")
    private String thumbnail;

    @Size(max = 255)
    @Nationalized
    @Column(name = "name")
    private String name;

    @Size(max = 50)
    @Nationalized
    @Column(name = "sku", length = 50)
    private String sku;

    @Column(name = "stock")
    private Integer stock;

    @Size(max = 400)
    @Nationalized
    @Column(name = "description", length = 400)
    private String description;

    @Nationalized
    @Lob
    @Column(name = "specs")
    private String specs;

    @Size(max = 50)
    @Nationalized
    @Column(name = "brand", length = 50)
    private String brand;

    @Column(name = "price")
    private BigDecimal price;

    @ColumnDefault("0")
    @Column(name = "sold")
    private Integer sold;

    @ColumnDefault("1")
    @Column(name = "status")
    private Boolean status;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private Set<ProductImage> productImages = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private Set<ProductTranslation> productTranslations = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<PromotionDetail> promotionDetails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<Rating> ratings = new LinkedHashSet<>();

    @Transient
    private Integer categoryId;
    @Transient
    private String categoryName;
    @Transient
    private Boolean categoryStatus;

    @Transient
    private Integer avgRating;

    @Transient
    private BigDecimal promotionPrice;

    @Transient
    private Integer discountPercentage;

    @Transient
    private Instant promotionEndDate;

    @Transient
    Object relatedProducts;

    @PostLoad
    public void handle() {
        if (this.sold == null) {
            this.sold = 0;
        }
        if (this.category != null) {
            this.categoryId = this.category.getId();
            this.categoryName = this.category.getName();
            this.categoryStatus = this.category.getStatus();
        }
        String locale = LocaleContextHolder.getLocale().getLanguage();
        if (this.productTranslations != null && !locale.equalsIgnoreCase("vi")) {
            var localizedProduct = productTranslations.stream()
                    .filter(productTranslation -> productTranslation.getLanguage().equals(locale))
                    .findFirst()
                    .orElse(null);
            if (localizedProduct != null) {
                if (localizedProduct.getTranslatedName() != null && !localizedProduct.getTranslatedName().isBlank()) {
                    this.setName(localizedProduct.getTranslatedName());
                }
                if (localizedProduct.getTranslatedDescription() != null && !localizedProduct.getTranslatedDescription().isBlank()) {
                    this.setDescription(localizedProduct.getTranslatedDescription());
                }
                if (localizedProduct.getTranslatedSpecs() != null && !localizedProduct.getTranslatedSpecs().isBlank()) {
                    this.setSpecs(localizedProduct.getTranslatedSpecs());
                }
            }
        }
        if (this.promotionDetails != null && !this.promotionDetails.isEmpty()) {
            Promotion highestDiscountPromotion = this.promotionDetails.stream()
                    .filter(detail -> {
                        Instant now = Instant.now();
                        return detail.getPromotion().getStartDate().isBefore(now) && 
                               detail.getPromotion().getEndDate().isAfter(now);
                    })
                    .map(PromotionDetail::getPromotion)
                    .max(Comparator.comparing(Promotion::getDiscountPercentage))
                    .orElse(null);
            
            if (highestDiscountPromotion != null && this.price != null) {
                this.discountPercentage = highestDiscountPromotion.getDiscountPercentage();
                this.promotionEndDate = highestDiscountPromotion.getEndDate();
                BigDecimal discountAmount = this.price.multiply(
                    BigDecimal.valueOf(this.discountPercentage)
                ).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                this.promotionPrice = this.price.subtract(discountAmount);
            } else {
                this.promotionPrice = this.price;
                this.discountPercentage = 0;
                this.promotionEndDate = null;
            }
        } else {
            this.promotionPrice = this.price;
            this.discountPercentage = 0;
        }
        if (this.ratings != null) {
            if (!this.ratings.isEmpty()) {
                double sum = 0;
                for (Rating rating : this.ratings) {
                    if (rating.getRating() != null) {
                        sum += rating.getRating();
                    }
                }
                float average = (float) (sum / this.ratings.size());
                
                int floorValue = (int) Math.floor(average);
                this.avgRating = (average >= (floorValue + 0.5f)) ? (floorValue + 1) : floorValue;
            } else {
                this.avgRating = 5;
            }
        } else {
            this.avgRating = 5;
        }
    }
    public Product toBasicResponse() {
        this.setProductTranslations(null);
        this.setPromotionDetails(null);
        this.setRatings(null);
        this.setProductImages(null);
        this.setCategory(null);
        return this;
    }

}
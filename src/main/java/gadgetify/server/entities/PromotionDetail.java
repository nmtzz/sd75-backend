package gadgetify.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "promotion_details")
public class PromotionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    @JsonIgnore
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @Transient
    private Integer productId;
    @Transient
    private String productName;
    @Transient
    private Integer productCategoryId;
    @Transient
    private String productCategoryName;
    @Transient
    private BigDecimal productPrice;

    @PostLoad
    public void handle() {
        if (this.product != null) {
            this.productId = this.product.getId();
            this.productName = this.product.getName();
            this.productPrice = this.product.getPrice();
            if (this.product.getCategory() != null) {
                this.productCategoryId = this.product.getCategory().getId();
                this.productCategoryName = this.product.getCategory().getName();
            }
        }
    }

}
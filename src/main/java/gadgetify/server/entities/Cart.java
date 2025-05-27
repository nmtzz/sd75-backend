package gadgetify.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @Transient
    private Integer productId;

    @Transient
    private String productName;
    @Transient
    private BigDecimal productPrice;
    @Transient
    private BigDecimal productPromotionPrice;
    @Transient
    private String productThumbnail;
    @Transient
    private Integer productCategoryId;
    @Transient
    private String productCategoryName;

    @Transient
    private Integer currentStock;

    @PostLoad
    public void handle() {
        if (this.product != null) {
            this.productId = this.product.getId();
            this.productName = this.product.getName();
            this.productPrice = this.product.getPrice();
            this.currentStock = this.product.getStock();
            this.productPromotionPrice = this.product.getPromotionPrice();
            this.productThumbnail = this.product.getThumbnail();
            if (this.product.getCategory() != null) {
                this.productCategoryId = this.product.getCategory().getId();
                this.productCategoryName = this.product.getCategory().getName();
            }
        }
    }

}
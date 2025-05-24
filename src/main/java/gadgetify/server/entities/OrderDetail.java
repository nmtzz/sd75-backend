package gadgetify.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price_at_purchase")
    private BigDecimal priceAtPurchase;

    @Transient
    private Integer productId;
    @Transient
    private String productName;
    @Transient
    private String productThumbnail;
    @Transient
    private Integer productCategoryId;
    @Transient
    private String productCategoryName;

    @Transient
    private Boolean isRated;

    public void handleIsRated() {
        if (this.order != null && this.order.getUser() != null && this.order.getUser().getRatings() != null) {
            var orderStatus = this.order.getStatus();
            if (orderStatus.equals("DELIVERED") || orderStatus.equals("RETURNED") || orderStatus.equals("REJECTED_RETURN") || orderStatus.equals("PENDING_RETURN_CONFIRMATION")) {
                var userRatings = this.order.getUser().getRatings();
                var userId = this.order.getUser().getId();
                if (userId != null) {
                    for (Rating rating : userRatings) {
                        if (rating.getProduct().getId().equals(this.product.getId()) && rating.getUser().getId() == userId) {
                            this.isRated = true;
                            return;
                        }
                    }
                }

            }

        }
        this.isRated = false;
    }

    @PostLoad
    public void handle() {
        if (this.product != null) {
            this.productId = this.product.getId();
            this.productName = this.product.getName();
            this.productThumbnail = this.product.getThumbnail();
            if (this.product.getCategory() != null) {
                this.productCategoryId = this.product.getCategory().getId();
                this.productCategoryName = this.product.getCategory().getName();
            }
        }
    }

}
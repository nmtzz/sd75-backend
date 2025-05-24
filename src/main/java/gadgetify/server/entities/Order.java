package gadgetify.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "voucher_id")
    @JsonIgnore
    private Voucher voucher;

    @Size(max = 50)
    @Nationalized
    @Column(name = "order_code", length = 50)
    private String orderCode;


    @Column(name = "subtotal")
    private BigDecimal subtotal;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Size(max = 40)
    @Nationalized
    @Column(name = "status", length = 40)
    private String status;

    @Size(max = 40)
    @Nationalized
    @Column(name = "payment_method", length = 40)
    private String paymentMethod;

    @Size(max = 70)
    @Nationalized
    @Column(name = "recipient_name", length = 70)
    private String recipientName;

    @Size(max = 25)
    @Nationalized
    @Column(name = "phone_number", length = 25)
    private String phoneNumber;

    @Nationalized
    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private Set<OrderDetail> orderDetails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Refund> refunds = new LinkedHashSet<>();

    @Transient
    private List<String> productThumbnails;


    @Transient
    private Integer voucherId;

    @Transient
    private Boolean needRefund;

    @Transient
    private String voucherCode;

    @Size(max = 15)
    @Nationalized
    @Column(name = "type", length = 15)
    private String type;


    @PostLoad
    public void handle() {
        if (this.voucher != null) {
            this.voucherId = this.voucher.getId();
            this.voucherCode = this.voucher.getCode();
        }
        if (this.refunds != null) {
            this.needRefund = this.refunds.stream().anyMatch(refund -> refund.getStatus().equals("PENDING"));
        } else {
            this.needRefund = false;
        }
    }

    public void setVoucherFromId() {
        if (this.voucherId != null) {
            this.voucher = new Voucher();
            this.voucher.setId(this.voucherId);
        }
    }
    public void fetchProductThumbnails() {
        if (this.orderDetails != null) {
            this.productThumbnails = this.orderDetails.stream()
                    .map(OrderDetail::getProductThumbnail)
                    .toList();
        }
    }

    public Order toBasicResponse() {
        this.setOrderDetails(null);
        this.setUser(null);
        this.setVoucher(null);
        this.shippingAddress = null;
        return this;
    }

}
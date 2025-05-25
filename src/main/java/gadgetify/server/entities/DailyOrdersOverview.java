package gadgetify.server.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "v_daily_orders_overview")
public class DailyOrdersOverview {
    @Id
    @Column(name = "total_orders")
    private Integer totalOrders;

    @Column(name = "unpaid_orders")
    private Integer unpaidOrders;

    @Column(name = "pending_confirmation_orders")
    private Integer pendingConfirmationOrders;

    @Column(name = "pending_shipping_orders")
    private Integer pendingShippingOrders;

    @Column(name = "shipping_orders")
    private Integer shippingOrders;

    @Column(name = "delivered_orders")
    private Integer deliveredOrders;

    @Column(name = "cancelled_orders")
    private Integer cancelledOrders;

    @Column(name = "returned_orders")
    private Integer returnedOrders;

    @NotNull
    @Column(name = "total_revenue", nullable = false)
    private BigDecimal totalRevenue;

    @NotNull
    @Column(name = "delivered_revenue", nullable = false)
    private BigDecimal deliveredRevenue;

    @NotNull
    @Column(name = "total_subtotal", nullable = false)
    private BigDecimal totalSubtotal;

    @NotNull
    @Column(name = "total_shipping_fee", nullable = false)
    private BigDecimal totalShippingFee;

    @NotNull
    @Column(name = "total_discount", nullable = false)
    private BigDecimal totalDiscount;

    @Column(name = "success_rate_percent")
    private Double successRatePercent;

}
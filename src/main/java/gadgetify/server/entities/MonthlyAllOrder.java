package gadgetify.server.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "v_monthly_all_orders")
public class MonthlyAllOrder {
    @Id
    @Size(max = 4000)
    @NotNull
    @Nationalized
    @Column(name = "thang_nam", nullable = false, length = 4000)
    private String thangNam;

    @Column(name = "all_orders")
    private Integer allOrders;

    @Column(name = "revenue")
    private BigDecimal revenue;

}
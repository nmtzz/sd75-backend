package gadgetify.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "vouchers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Nationalized
    @Column(name = "code", length = 50)
    private String code;

    @Size(max = 20)
    @Nationalized
    @Column(name = "discount_type", length = 20)
    private String discountType;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "min_order_amount")
    private BigDecimal minOrderAmount;

    @Column(name = "max_order_amount")
    private BigDecimal maxOrderAmount;

    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "quantity")
    private Integer quantity;

    @ColumnDefault("0")
    @Column(name = "used")
    private Integer used;

    @ColumnDefault("1")
    @Column(name = "status")
    private Boolean status;

    @OneToMany(mappedBy = "voucher")
    @JsonIgnore
    private Set<Order> orders = new LinkedHashSet<>();

}
package gadgetify.server.repositories;

import gadgetify.server.entities.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

    Optional<Voucher> findByCode(String code);

    @Query("SELECT v FROM Voucher v WHERE v.status = true AND v.startDate <= ?1 AND v.endDate >= ?1 AND (v.quantity > v.used OR v.quantity = -1)")
    List<Voucher> findAllActiveVouchers(Instant now);

    @Query("SELECT v FROM Voucher v WHERE v.status = ?1 ORDER BY v.endDate DESC")
    List<Voucher> findAllByStatusOrderByEndDateDesc(Boolean status);

    boolean existsByCode(String code);
} 
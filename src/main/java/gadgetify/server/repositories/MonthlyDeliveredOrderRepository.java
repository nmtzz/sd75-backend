package gadgetify.server.repositories;

import gadgetify.server.entities.MonthlyDeliveredOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyDeliveredOrderRepository extends JpaRepository<MonthlyDeliveredOrder, String> {
}

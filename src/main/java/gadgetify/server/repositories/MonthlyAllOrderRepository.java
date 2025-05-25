package gadgetify.server.repositories;

import gadgetify.server.entities.MonthlyAllOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyAllOrderRepository extends JpaRepository<MonthlyAllOrder, String> {
}

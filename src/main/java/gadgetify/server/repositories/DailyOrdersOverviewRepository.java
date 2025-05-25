package gadgetify.server.repositories;

import gadgetify.server.entities.DailyOrdersOverview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyOrdersOverviewRepository extends JpaRepository<DailyOrdersOverview, Integer> {
}

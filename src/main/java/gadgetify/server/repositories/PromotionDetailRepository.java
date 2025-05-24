package gadgetify.server.repositories;

import gadgetify.server.entities.PromotionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionDetailRepository  extends JpaRepository<PromotionDetail, Integer> {
    void deleteAllByPromotionId(Integer id);
}

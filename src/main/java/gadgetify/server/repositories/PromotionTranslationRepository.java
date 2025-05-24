package gadgetify.server.repositories;

import gadgetify.server.entities.PromotionTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionTranslationRepository extends JpaRepository<PromotionTranslation, Integer> {
    void deleteAllByPromotion_Id(Integer promotionId);
}

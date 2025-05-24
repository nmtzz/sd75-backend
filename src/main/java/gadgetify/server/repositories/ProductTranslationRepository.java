package gadgetify.server.repositories;

import gadgetify.server.entities.ProductTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductTranslationRepository extends JpaRepository<ProductTranslation, Integer> {
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductTranslation pt WHERE pt.product.id = :productId")
    void deleteAllByProductId(@Param("productId") Integer productId);
}

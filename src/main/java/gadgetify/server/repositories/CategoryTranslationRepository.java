package gadgetify.server.repositories;

import gadgetify.server.entities.CategoryTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryTranslationRepository extends JpaRepository<CategoryTranslation, Integer> {
}

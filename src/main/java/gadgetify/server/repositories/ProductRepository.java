package gadgetify.server.repositories;

import gadgetify.server.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository  extends JpaRepository<Product, Integer> {

    @Query(nativeQuery = true, value = "SELECT DISTINCT brand FROM products")
    List<String> findAllUniqueBrands();

    @Query(nativeQuery = true, value = "SELECT specs FROM products")
    List<String> findAllSpecs();

    @Query(nativeQuery = true, value = "SELECT p.* FROM products p WHERE p.id != :productId AND p.category_id = :categoryId" )
    List<Product> findRelatedProducts(Integer productId, Integer categoryId);

    @Query(nativeQuery = true, value = "SELECT translated_specs FROM product_translations WHERE language = :language")
    List<String> findAllSpecsByLanguage(@Param("language") String language);

    List<Product> findAllByIdIn(List<Integer> ids);

    @Query(nativeQuery = true, value = "SELECT p.* FROM products p JOIN product_translations pt ON p.id = pt.product_id WHERE p.name LIKE %:name% OR pt.translated_name LIKE %:name%")
    Page<Product> findAllByProductName(@Param("name") String name, Pageable pageable);
}

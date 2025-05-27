package gadgetify.server.repositories;

import gadgetify.server.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO carts (user_id, product_id, quantity) VALUES (:userId, :productId, :quantity)")
    void addToCart(Integer userId, Integer productId, Integer quantity);

    void deleteByUserId(int userId);

    Optional<Cart> findCartByUser_IdAndProduct_Id(Integer userId, Integer productId);

    List<Cart> findAllByUserId(Integer userId);
}

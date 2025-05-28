package gadgetify.server.repositories;

import gadgetify.server.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findAllByUser_Id(Integer userId);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE notifications SET is_read = 1 WHERE user_id = ?1")
    void markAllAsRead(Integer userId);
}

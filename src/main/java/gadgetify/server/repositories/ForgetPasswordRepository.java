package gadgetify.server.repositories;

import gadgetify.server.entities.ForgetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgetPasswordRepository extends JpaRepository<ForgetPassword, Integer> {

    void deleteByUser_Id(Integer userId);

    Optional<ForgetPassword> findByUser_Email(String userEmail);
}

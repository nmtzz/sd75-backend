package gadgetify.server.configs;

import gadgetify.server.entities.User;
import gadgetify.server.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationRunnerConfig {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                var user = new User();
                user.setEmail("admin");
                user.setPassword(passwordEncoder.encode("123"));
                user.setRole("ADMIN");
                user.setFullName("Admin");
                user.setStatus(true);
                userRepository.save(user);
                log.info("Admin user created with email: admin and password: 123");
            }
        };
    }
}

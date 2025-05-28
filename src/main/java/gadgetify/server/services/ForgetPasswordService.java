package gadgetify.server.services;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.ForgetPassword;
import gadgetify.server.repositories.ForgetPasswordRepository;
import gadgetify.server.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgetPasswordService {
    private final ForgetPasswordRepository forgetPasswordRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Response<Void> sendPin(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        forgetPasswordRepository.deleteByUser_Id(user.getId());

        String pin = generateRandomPin();

        ForgetPassword forgetPassword = new ForgetPassword();
        forgetPassword.setUser(user);
        forgetPassword.setPin(pin);
        forgetPassword.setCreatedAt(Instant.now());
        forgetPassword.setExpiredAt(Instant.now().plusSeconds(15 * 60));
        forgetPassword.setAttemptCount(0);
        forgetPassword.setIsVerified(false);

        forgetPasswordRepository.save(forgetPassword);

        try {
            emailService.sendForgetPasswordPinEmailAsync(email, pin, user.getFullName());
            log.info("Forget password PIN email queued for sending to {}", email);
        } catch (Exception e) {
            log.error("Error queuing forget password PIN email", e);
            throw new RuntimeException("Failed to send email");
        }

        return Response.<Void>builder()
                .build();
    }
    public Response<Void> verifyPin(String email, String pin) {
        ForgetPassword forgetPassword = forgetPasswordRepository.findByUser_Email(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        if (forgetPassword.getExpiredAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("PIN expired");
        }
        if (forgetPassword.getAttemptCount() == null) {
            forgetPassword.setAttemptCount(0);
        }
        if (forgetPassword.getAttemptCount() >= 5) {
            throw new IllegalArgumentException("Too many attempts");
        }
        if (!forgetPassword.getPin().equals(pin)) {
            forgetPassword.setAttemptCount(forgetPassword.getAttemptCount() + 1);
            forgetPasswordRepository.save(forgetPassword);
            throw new IllegalArgumentException("Invalid PIN");
        }
        forgetPassword.setIsVerified(true);
        forgetPasswordRepository.save(forgetPassword);
        return Response.<Void>builder()
                .build();

    }
    public Response<Void> recoverPassword(String email, String password) {
        var forgetPassword = forgetPasswordRepository.findByUser_Email(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));
        if (!forgetPassword.getIsVerified()) {
            throw new IllegalArgumentException("PIN not verified");
        }
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return Response.<Void>builder()
                .build();
    }

    private String generateRandomPin() {
        Random random = new Random();
        int pin = 10000 + random.nextInt(90000);
        return String.valueOf(pin);
    }
}

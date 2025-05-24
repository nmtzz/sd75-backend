package gadgetify.server.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import gadgetify.server.dtos.Response;
import gadgetify.server.entities.User;
import gadgetify.server.repositories.UserRepository;
import gadgetify.server.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    public Response<User> login(User user) {
        var userSearched = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (userSearched.getPassword() == null) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        if (!passwordEncoder.matches(user.getPassword(), userSearched.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        var token = tokenService.createToken(userSearched);
        userSearched.setToken(token);
        return Response.<User>builder()
                .result(userSearched.toBasicResponse())
                .build();
    }
    public Response<User> loginWithGoogle(User user) {
        var savedUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        if (savedUser == null) {
            user.setRole("CUSTOMER");
            user.setStatus(true);
            savedUser = userRepository.save(user);
        }
        var token = tokenService.createToken(savedUser);
        savedUser.setToken(token);
        return Response.<User>builder()
                .result(savedUser.toBasicResponse())
                .build();
    }

    public Response<User> getAuthentication() {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId == null || userId.equals("anonymousUser")) {
            return Response.<User>builder()
                    .success(false)
                    .result(null)
                    .build();
        }
        var user = userRepository.findById(Integer.parseInt(userId)).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        return Response.<User>builder()
                .result(user.toBasicResponse())
                .build();
    }

    public Response<User> save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var savedUser = userRepository.save(user);
        savedUser.setToken(tokenService.createToken(savedUser));
        return Response.<User>builder()
                .result(savedUser.toBasicResponse())
                .build();
    }

    public Response<List<User>> findAll() {
        var users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        users.forEach(User::toBasicResponse);
        return Response.<List<User>>builder()
                .result(users)
                .build();
    }

    public Response<Void> changeStatus(Integer id) {
        var user = userRepository.findById(id)
                .orElse(null);
        if (user != null) {
            user.setStatus(!user.getStatus());
            userRepository.save(user);
        }
        return Response.<Void>builder()
                .build();
    }
    @Transactional
    public Response<Void> resetPassword(Integer id, String password) {
        var user = userRepository.findById(id)
                .orElse(null);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }
        return Response.<Void>builder()
                .build();
    }

    @Transactional
    public Response<Void> changePassword(String oldPassword, String newPassword) {
        var userId = SecurityUtil.getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElse(null);
        if (user != null) {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new IllegalArgumentException("Invalid old password");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
        return Response.<Void>builder()
                .build();
    }
}

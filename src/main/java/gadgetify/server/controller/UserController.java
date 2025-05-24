package gadgetify.server.controller;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.User;
import gadgetify.server.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public Response<User> login(@RequestBody User user) {
        return userService.login(user);
    }

    @PostMapping("/login/google")
    public Response<User> loginWithGoogle(@RequestBody User user) {
        return userService.loginWithGoogle(user);
    }

    @PostMapping("/register")
    public Response<User> register(@RequestBody @Valid User user) {
        return userService.save(user);
    }

    @GetMapping("/auth")
    public Response<User> getAuthentication() {
        return userService.getAuthentication();
    }
    @PostMapping
    public Response<User> save(@RequestBody @Valid User user) {
        return userService.save(user);
    }
    @GetMapping
    public Response<List<User>> findAll() {
        return userService.findAll();
    }
    @PatchMapping("/change-status/{id}")
    public Response<Void> changeStatus(@PathVariable Integer id) {
        return userService.changeStatus(id);
    }
    @PatchMapping("/reset-password/{id}")
    public Response<Void> resetPassword(@PathVariable Integer id, @RequestParam String password) {
        return userService.resetPassword(id, password);
    }
    @PatchMapping("/change-password")
    public Response<Void> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        return userService.changePassword(oldPassword, newPassword);
    }
}

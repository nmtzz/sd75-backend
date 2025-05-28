package gadgetify.server.controller;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.Notification;
import gadgetify.server.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public Response<List<Notification>> findAllByUserId() {
        return notificationService.findAllByUserId();
    }

    @PatchMapping("/read-all")
    public Response<Void> readAll() {
        return notificationService.readAll();
    }

    @PatchMapping("/read/{id}")
    public Response<Void> read(@PathVariable Integer id) {
        return notificationService.markAsRead(id);
    }
}

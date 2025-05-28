package gadgetify.server.services;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.Notification;
import gadgetify.server.repositories.NotificationRepository;
import gadgetify.server.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Response<List<Notification>> findAllByUserId() {
        var userId = SecurityUtil.getCurrentUserId();
        var notifications = notificationRepository.findAllByUser_Id(userId);
        return Response.<List<Notification>>builder()
                .result(notifications)
                .build();
    }

    @Transactional
    public Response<Void> readAll() {
        var userId = SecurityUtil.getCurrentUserId();
        notificationRepository.markAllAsRead(userId);
        return Response.<Void>builder()
                .build();
    }

    public Response<Void> markAsRead(Integer id) {
        var notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
        return Response.<Void>builder()
                .build();
    }
}

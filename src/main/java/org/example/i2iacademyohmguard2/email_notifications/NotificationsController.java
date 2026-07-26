package org.example.i2iacademyohmguard2.email_notifications;

import lombok.RequiredArgsConstructor;
import org.example.i2iacademyohmguard2.email_notifications.dto.NotificationDto;
import org.example.i2iacademyohmguard2.users.UsersService;
import org.example.i2iacademyohmguard2.users.UsersTable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationsController {

    private final EmailNotificationsService emailNotificationsService;
    private final UsersService usersService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> notifications() {
        return ResponseEntity.ok(emailNotificationsService.recentNotifications());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<NotificationDto>> notificationsMine(@RequestHeader("Authorization") String token) {
        UsersTable user = usersService.findByToken(token);
        return ResponseEntity.ok(emailNotificationsService.recentNotificationsForUser(user));
    }
}

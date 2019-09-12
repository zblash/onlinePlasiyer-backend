package com.marketing.web.controllers;

import com.marketing.web.dtos.notification.ReadableNotification;
import com.marketing.web.dtos.notification.WritableNotification;
import com.marketing.web.models.Notification;
import com.marketing.web.models.User;
import com.marketing.web.services.notification.NotificationService;
import com.marketing.web.services.notification.NotificationServiceImpl;
import com.marketing.web.services.user.UserService;
import com.marketing.web.services.user.UserServiceImpl;
import com.marketing.web.utils.mappers.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<ReadableNotification>> getAllNotifications(){
        User user = userService.getLoggedInUser();
        return ResponseEntity.ok(notificationService.findAllByUser(user).stream()
                .map(NotificationMapper::notificationToReadableNotification).collect(Collectors.toList()));

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ReadableNotification> createNotification(@Valid @RequestBody WritableNotification writableNotification){
        Notification notification = new Notification();
        notification.setMessage(writableNotification.getMessage());
        notification.setTitle(writableNotification.getTitle());
        notification.setUser(userService.findByUUID(writableNotification.getUserId()));
        return ResponseEntity.ok(
                NotificationMapper.notificationToReadableNotification(notificationService.create(notification)));
    }
}

package com.marketing.web.services.notification;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Notification;
import com.marketing.web.models.User;
import com.marketing.web.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;


    @Override
    public List<Notification> findAllByUser(User user) {
        return notificationRepository.findAllByUser(user);
    }

    @Override
    public List<Notification> findAll() {
       return notificationRepository.findAll();
    }

    @Override
    public Notification findByUUID(String uuid) {
        return notificationRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"notification",uuid));
    }

    @Override
    public Notification create(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public void delete(Notification notification) {
        notificationRepository.delete(notification);
    }
}

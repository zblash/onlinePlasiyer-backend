package com.marketing.web.services.notification;

import com.marketing.web.models.Notification;
import com.marketing.web.models.User;
import com.marketing.web.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;


    @Override
    public List<Notification> findAllByUser(User user) {
        return notificationRepository.findAllByUser(user);
    }

    @Override
    public Notification create(Notification notification) {
        return notificationRepository.save(notification);
    }
}

package com.marketing.web.services.notification;

import com.marketing.web.models.Notification;
import com.marketing.web.models.User;

import java.util.List;

public interface NotificationService {

    List<Notification> findAllByUser(User user);

    List<Notification> findAll();

    Notification findByUUID(String uuid);

    Notification create(Notification notification);

    void delete(Notification notification);

}

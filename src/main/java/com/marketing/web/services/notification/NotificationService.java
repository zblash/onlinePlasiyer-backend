package com.marketing.web.services.notification;

import com.marketing.web.models.Notification;
import com.marketing.web.models.User;

import java.util.List;

public interface NotificationService {

    List<Notification> findAllByUser(User user);

    Notification create(Notification notification);


}
